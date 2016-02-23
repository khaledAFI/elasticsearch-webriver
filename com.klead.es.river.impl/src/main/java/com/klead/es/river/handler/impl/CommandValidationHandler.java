package com.klead.es.river.handler.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.ResultCode;
import com.klead.es.river.exception.BusinessException;
import com.klead.es.river.handler.ICommandValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by kafi on 18/02/2016.
 */
@Component
public class CommandValidationHandler implements ICommandValidationHandler {
    @Autowired
    private String indexTypeMapping;

    @Override
    public void validate(IndexationCommand command) throws BusinessException {
        if (command != null) {
            if (command.getIndexationPacketSize() == null) {
                throw new BusinessException(ResultCode.INDEXATION_PACKET_SIZE_EMPTY.name());
            }
            if (command.getIndexationPacketSize().equals(0)) {
                throw new BusinessException(ResultCode.INDEXATION_PACKET_SIZE_ILLEGAL.name());
            }
            if (StringUtils.isEmpty(command.getIndexName())){
                throw new BusinessException(ResultCode.INDEX_NAME_EMPTY.name());
            }
            if (StringUtils.isEmpty(command.getIndexType())){
                throw new BusinessException(ResultCode.INDEX_TYPE_EMPTY.name());
            }
            if (StringUtils.isEmpty(command.getReplicaNumber())){
                throw new BusinessException(ResultCode.REPLICA_NUMBER_EMPTY.name());
            }
            if (StringUtils.isEmpty(command.getShardNumber())){
            throw new BusinessException(ResultCode.SHARD_NUMBER_EMPTY.name());
            }
            if (StringUtils.isEmpty(command.getRoutingColumn())){
                throw new BusinessException(ResultCode.ROUTING_COLUMN_EMPTY.name());
            }else if (!indexTypeMapping.contains(indexTypeMapping)){
                throw new BusinessException(ResultCode.ROUTING_COLUMN_UNKNOWN.name());
            }
        } else {
            throw new BusinessException(ResultCode.INDEXATION_COMMAND_EMPTY.name());
        }
    }
}
