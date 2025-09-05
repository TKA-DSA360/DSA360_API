package com.dsa360.api.serviceimpl;

import com.dsa360.api.dao.SubscriptionDAO;
import com.dsa360.api.dto.SubscriptionDTO;
import com.dsa360.api.entity.master.SubscriptionEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.SubscriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionDAO subscriptionDAO;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        if (subscriptionDTO.getSubscriptionId() == null) {
            subscriptionDTO.setSubscriptionId(UUID.randomUUID().toString());
        }
        SubscriptionEntity entity = modelMapper.map(subscriptionDTO, SubscriptionEntity.class);
        subscriptionDAO.save(entity);
        return modelMapper.map(entity, SubscriptionDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getSubscriptionsByTenant(String tenantId) {
        List<SubscriptionEntity> entities = subscriptionDAO.findByTenantId(tenantId);
        return entities.stream()
                .map(entity -> modelMapper.map(entity, SubscriptionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscription(String subscriptionId, String tenantId) {
        SubscriptionEntity entity = subscriptionDAO.findByIdAndTenantId(subscriptionId, tenantId)
                .orElseThrow(() -> new SomethingWentWrongException("Subscription not found: " + subscriptionId));
        return modelMapper.map(entity, SubscriptionDTO.class);
    }

    @Override
    @Transactional
    public SubscriptionDTO updateSubscription(SubscriptionDTO subscriptionDTO) {
        SubscriptionEntity entity = subscriptionDAO.findByIdAndTenantId(subscriptionDTO.getSubscriptionId(), subscriptionDTO.getTenantId())
                .orElseThrow(() -> new SomethingWentWrongException("Subscription not found: " + subscriptionDTO.getSubscriptionId()));
        modelMapper.map(subscriptionDTO, entity);
        subscriptionDAO.update(entity);
        return modelMapper.map(entity, SubscriptionDTO.class);
    }

    @Override
    @Transactional
    public void deleteSubscription(String subscriptionId, String tenantId) {
        subscriptionDAO.delete(subscriptionId, tenantId);
    }
}