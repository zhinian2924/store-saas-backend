package com.example.storesaas.mini.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.ResultCode;
import com.example.storesaas.customer.entity.Customer;
import com.example.storesaas.customer.mapper.CustomerMapper;
import com.example.storesaas.mini.dto.WechatLoginDTO;
import com.example.storesaas.mini.vo.MiniLoginVO;
import com.example.storesaas.miniappconfig.MiniappConfigService;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.LoginUser;
import com.example.storesaas.mini.dto.MockLoginDTO;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MiniAuthService {
    private final CustomerMapper customerMapper;
    private final TenantMapper tenantMapper;
    private final MiniappConfigService configService;
    private final WechatClient wechatClient;

    public MiniAuthService(CustomerMapper customerMapper, TenantMapper tenantMapper,
                           MiniappConfigService configService, WechatClient wechatClient) {
        this.customerMapper = customerMapper;
        this.tenantMapper = tenantMapper;
        this.configService = configService;
        this.wechatClient = wechatClient;
    }

    @Transactional
    public MiniLoginVO wechatLogin(WechatLoginDTO request) {
        var miniapp = configService.requireActiveByAppId(request.appId());
        var wechatSession = wechatClient.exchange(miniapp.appId(), miniapp.appSecret(), request.code());
        Customer customer = findOrCreate(miniapp.tenantId(), wechatSession.openid());
        return createSession(customer);
    }

    @Transactional
    public MiniLoginVO mockLogin(MockLoginDTO request) {
        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getId, request.tenantId()).eq(Tenant::getDeleted, 0));
        if (tenant == null || !Integer.valueOf(com.example.storesaas.tenant.TenantStatus.ACTIVE).equals(tenant.getStatus())) {
            throw new BusinessException("门店不存在或未启用");
        }
        return createSession(findOrCreate(request.tenantId(), request.openid()));
    }

    private Customer findOrCreate(Long tenantId, String openid) {
        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getTenantId, tenantId)
                .eq(Customer::getOpenid, openid)
                .eq(Customer::getDeleted, DeleteStatus.NOT_DELETED));
        if (customer == null) {
            customer = new Customer();
            customer.setTenantId(tenantId);
            customer.setOpenid(openid);
            customer.setStatus(CommonStatus.ENABLED);
            fill(customer);
            customerMapper.insert(customer);
        } else if (!Integer.valueOf(CommonStatus.ENABLED).equals(customer.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "消费者账号已停用");
        }
        return customer;
    }

    private MiniLoginVO createSession(Customer c) {
        StpUtil.login("CUSTOMER:" + c.getId());
        StpUtil.getSession().set("loginUser", new LoginUser(c.getId(), c.getTenantId(), AccountType.CUSTOMER,
                c.getOpenid(), null, List.of()));
        SaTokenInfo t = StpUtil.getTokenInfo();
        return new MiniLoginVO(t.getTokenName(), t.getTokenValue(), c.getId(), c.getTenantId(),
                c.getNickname(), c.getAvatarUrl());
    }

    private void fill(Customer c) {
        var n = LocalDateTime.now();
        c.setCreatedAt(n);
        c.setUpdatedAt(n);
        c.setDeleted(DeleteStatus.NOT_DELETED);
    }
}
