package com.abcfinancial.poc.s1.service;

import java.util.UUID;

public interface PatchService
{
    String getEntity( UUID id );
    boolean putEntity(UUID id, String entity);
}
