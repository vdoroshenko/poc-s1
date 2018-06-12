package com.abcfinancial.poc.s1.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatchServiceImpl implements PatchService
{
    @Override
    public String getEntity( UUID id )
    {
        // STUB
        return "";
    }

    @Override
    public boolean putEntity( UUID id, String entity )
    {
        // STUB
        return true;
    }
}
