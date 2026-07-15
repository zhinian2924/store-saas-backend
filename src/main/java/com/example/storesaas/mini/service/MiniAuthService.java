package com.example.storesaas.mini.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.customer.entity.Customer;
import com.example.storesaas.customer.mapper.CustomerMapper;
import com.example.storesaas.security.AccountType;
import com.example.storesaas.security.LoginUser;
import com.example.storesaas.mini.dto.MockLoginRequest;
import com.example.storesaas.tenant.entity.Tenant;
import com.example.storesaas.tenant.mapper.TenantMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MiniAuthService {
    private final CustomerMapper customerMapper;
    private final TenantMapper tenantMapper;

    public MiniAuthService(CustomerMapper customerMapper, TenantMapper tenantMapper) {
        this.customerMapper = customerMapper;
        this.tenantMapper = tenantMapper;
    }

    public Map<String, Object> mockLogin(MockLoginRequest request) {
        Tenant tenant = tenantMapper.selectOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getId, request.tenantId()).eq(Tenant::getDeleted, 0));
        if (tenant == null) throw new BusinessException("门店不存在");
        Customer c = customerMapper.selectOne(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getTenantId, request.tenantId())
                .eq(Customer::getOpenid, request.openid())
                .eq(Customer::getDeleted, 0));
        if (c == null) {
            c = new Customer();
            c.setTenantId(request.tenantId());
            c.setOpenid(request.openid());
            c.setNickname("微信用户");
            c.setStatus(1);
            fill(c);
            customerMapper.insert(c);
        }
        StpUtil.login(c.getId());
        StpUtil.getSession().set("loginUser", new LoginUser(c.getId(), c.getTenantId(), AccountType.CUSTOMER,
                c.getOpenid(), null, List.of()));
        SaTokenInfo t = StpUtil.getTokenInfo();
        return Map.of("tokenName", t.getTokenName(), "tokenValue", t.getTokenValue(), "customerId",
                c.getId(), "tenantId", c.getTenantId());
    }

    private void fill(Customer c) {
        var n = LocalDateTime.now();
        c.setCreatedAt(n);
        c.setUpdatedAt(n);
        c.setDeleted(DeleteStatus.NOT_DELETED);
    }
}
