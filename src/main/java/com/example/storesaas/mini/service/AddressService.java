package com.example.storesaas.mini.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.mini.CustomerContext;
import com.example.storesaas.mini.dto.AddressRequest;
import com.example.storesaas.mini.entity.CustomerAddress;
import com.example.storesaas.mini.mapper.CustomerAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {
    private final CustomerAddressMapper mapper;

    public AddressService(CustomerAddressMapper mapper) {
        this.mapper = mapper;
    }

    public List<CustomerAddress> list() {
        return mapper.selectList(query().orderByDesc(CustomerAddress::getIsDefault).orderByDesc(CustomerAddress::getId));
    }

    @Transactional
    public CustomerAddress create(AddressRequest r) {
        CustomerAddress a = new CustomerAddress();
        copy(a, r);
        a.setTenantId(CustomerContext.tenantId());
        a.setCustomerId(CustomerContext.customerId());
        fill(a);
        if (Boolean.TRUE.equals(r.isDefault()) || list().isEmpty()) makeDefault(a);
        mapper.insert(a);
        return a;
    }

    @Transactional
    public CustomerAddress update(Long id, AddressRequest r) {
        CustomerAddress a = owned(id);
        copy(a, r);
        a.setUpdatedAt(LocalDateTime.now());
        if (Boolean.TRUE.equals(r.isDefault())) makeDefault(a);
        mapper.updateById(a);
        return a;
    }

    @Transactional
    public void remove(Long id) {
        CustomerAddress a = owned(id);
        a.setDeleted(DeleteStatus.DELETED);
        a.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(a);
    }

    @Transactional
    public CustomerAddress setDefault(Long id) {
        CustomerAddress a = owned(id);
        makeDefault(a);
        mapper.updateById(a);
        return a;
    }

    private CustomerAddress owned(Long id) {
        CustomerAddress a = mapper.selectOne(query().eq(CustomerAddress::getId, id));
        if (a == null) throw new BusinessException("地址不存在");
        return a;
    }

    private LambdaQueryWrapper<CustomerAddress> query() {
        return new LambdaQueryWrapper<CustomerAddress>().eq(CustomerAddress::getTenantId, CustomerContext.tenantId()).eq(CustomerAddress::getCustomerId, CustomerContext.customerId()).eq(CustomerAddress::getDeleted, DeleteStatus.NOT_DELETED);
    }

    private void copy(CustomerAddress a, AddressRequest r) {
        a.setConsignee(r.consignee());
        a.setPhone(r.phone());
        a.setProvince(r.province());
        a.setCity(r.city());
        a.setDistrict(r.district());
        a.setDetail(r.detail());
        a.setIsDefault(Boolean.TRUE.equals(r.isDefault()) ? 1 : 0);
    }

    private void fill(CustomerAddress a) {
        LocalDateTime n = LocalDateTime.now();
        a.setCreatedAt(n);
        a.setUpdatedAt(n);
        a.setDeleted(DeleteStatus.NOT_DELETED);
    }

    private void makeDefault(CustomerAddress a) {
        mapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CustomerAddress>().eq(CustomerAddress::getTenantId, CustomerContext.tenantId()).eq(CustomerAddress::getCustomerId, CustomerContext.customerId()).eq(CustomerAddress::getDeleted, DeleteStatus.NOT_DELETED).set(CustomerAddress::getIsDefault, 0));
        a.setIsDefault(1);
    }
}
