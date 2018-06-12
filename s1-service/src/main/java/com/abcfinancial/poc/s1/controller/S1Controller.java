package com.abcfinancial.poc.s1.controller;

import com.abcfinancial.poc.s1.consts.PatchType;
import com.abcfinancial.poc.s1.service.PatchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.json.*;
import javax.validation.Valid;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/entity")
public class S1Controller
{
    @Autowired
    private PatchService patchService;

    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @RequestMapping(value="/v1/{id}", method = RequestMethod.PATCH, consumes = PatchType.PATCH_UTF8 /*, produces = "application/json;charset=UTF-8"*/)
    @ResponseBody
    public JsonNode patchEntityRFC(@PathVariable( "id" ) UUID id, @Valid @RequestBody String patch) throws IOException
    {
        String entity = patchService.getEntity( id );

        ObjectMapper jackson = new ObjectMapper();
        JsonNode entityNode = jackson.readTree(entity);
        JsonNode jsonPatch = jackson.readTree(patch);

        JsonNode patchedNode = JsonPatch.apply(jsonPatch, entityNode);

        patchService.putEntity( id, patchedNode.asText());

        return patchedNode;

    }

    @RequestMapping(value="/v2/{id}", method = RequestMethod.PATCH, consumes = PatchType.PATCH_UTF8)
    @ResponseBody
    public JsonNode patchEntityJSR(@PathVariable( "id" ) UUID id, @Valid @RequestBody String jsonPatch)  throws IOException
    {

        String entity = patchService.getEntity( id );

        JsonObject target;
        JsonArray patch;

        try(JsonReader jr = Json.createReader(new StringReader(entity)))
        {
            target = jr.readObject();
        }

        try(JsonReader jr = Json.createReader(new StringReader(jsonPatch)))
        {
            patch = jr.readArray();
        }

        javax.json.JsonPatch patcher = Json.createPatchBuilder(patch).build();
        JsonStructure output = patcher.apply(target);
        String result = output.toString();

        ObjectMapper jackson = new ObjectMapper();
        return jackson.readTree(result);
    }

    @RequestMapping(value="/v2/{id}", method = RequestMethod.PATCH, consumes = PatchType.MERGE_UTF8)
    @ResponseBody
    public JsonNode mergeEntityJSR(@PathVariable( "id" ) UUID id, @Valid @RequestBody String jsonMerge) throws IOException
    {
        String entity = patchService.getEntity( id );

        JsonObject target;
        JsonObject merge;

        try(JsonReader jr = Json.createReader(new StringReader(entity)))
        {
            target = jr.readObject();
        }

        try(JsonReader jr = Json.createReader(new StringReader(jsonMerge)))
        {
            merge = jr.readObject();
        }

        JsonValue output = Json.createMergePatch(merge).apply(target);
        String result = output.toString();

        ObjectMapper jackson = new ObjectMapper();
        return jackson.readTree(result);
    }

}
