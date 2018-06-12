package com.abcfinancial.poc.s1.controller;

import com.abcfinancial.poc.s1.consts.PatchType;
import com.abcfinancial.poc.s1.service.PatchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.is;

@RunWith( SpringRunner.class )
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class S1ControllerTest
{
    private MockMvc mockMvc;

    @InjectMocks
    private S1Controller controller;

    @Mock
    private PatchService patchService;

    private final static UUID entityID = UUID.randomUUID();

    private final static UUID entityID2 = UUID.randomUUID();

    private final static String JSON = "{\"flavors\": [\"apple\", \"banana\", \"cherry\"], \"cart\":{\"count\":\"three\"}}";
    private final static String PATCH = "[{\"op\": \"remove\", \"path\": \"/flavors/1\"}]";
    private final static String MERGE = "{\"flavors\": [\"apple\", \"rose\", \"violet\"],\"size\":5,\"cart\":{\"count\":\"one\"}}";

    private final static String TARGET = "{\n" +
                                         "     \"title\": \"Goodbye!\",\n" +
                                         "     \"author\" : {\n" +
                                         "       \"givenName\" : \"John\",\n" +
                                         "       \"familyName\" : \"Doe\"\n" +
                                         "     },\n" +
                                         "     \"tags\":[ \"example\", \"sample\" ],\n" +
                                         "     \"content\": \"This will be unchanged\"\n" +
                                         "}";
    private final static String MERGE_PATCH = "{\n" +
                                              "     \"title\": \"Hello!\",\n" +
                                              "     \"phoneNumber\": \"+01-123-456-7890\",\n" +
                                              "     \"author\": {\n" +
                                              "       \"familyName\": null\n" +
                                              "     },\n" +
                                              "     \"tags\": [ \"example\" ]\n" +
                                              "}";
    private final static String MERGE_EXPECTED = "{\n" +
                                                 "     \"title\": \"Hello!\",\n" +
                                                 "     \"author\" : {\n" +
                                                 "       \"givenName\" : \"John\"\n" +
                                                 "     },\n" +
                                                 "     \"tags\": [ \"example\" ],\n" +
                                                 "     \"content\": \"This will be unchanged\",\n" +
                                                 "     \"phoneNumber\": \"+01-123-456-7890\"\n" +
                                                 "   }";

    @Before
    public void setup()
    {
        initMocks( this );
        this.mockMvc = MockMvcBuilders.standaloneSetup( controller ).build();
        when( patchService.getEntity( entityID ) ).thenReturn( JSON );
        when( patchService.getEntity( entityID2 ) ).thenReturn( TARGET );
    }

    @Test
    public void shouldPatchEntityByRFC() throws Exception
    {
        mockMvc.perform( patch( "/entity/v1/{id}", entityID ).contentType( MediaType.valueOf( PatchType.PATCH ) ).characterEncoding( "UTF-8" ).content( PATCH ) )
               .andDo( print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "flavors[1]", is( "cherry" ) ) )
               .andExpect( jsonPath( "flavors", hasSize( 2 ) )
               );
    }

    @Test
    public void shouldPatchEntityByJSR() throws Exception
    {
        mockMvc.perform( patch( "/entity/v2/{id}", entityID ).contentType( MediaType.valueOf( PatchType.PATCH ) ).characterEncoding( "UTF-8" ).content( PATCH ) )
               .andDo( print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "flavors[1]", is( "cherry" ) ) )
               .andExpect( jsonPath( "flavors", hasSize( 2 ) )
               );
    }

    @Test
    public void shouldMergeEntityByJSR() throws Exception
    {
        mockMvc.perform( patch( "/entity/v2/{id}", entityID ).contentType( MediaType.valueOf( PatchType.MERGE ) ).characterEncoding( "UTF-8" ).content( MERGE ) )
               .andDo( print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "flavors[1]", is( "rose" ) ) )
               .andExpect( jsonPath( "flavors", hasSize( 3 ) ) )
               .andExpect( jsonPath( "size", is( 5 ) ) )
               .andExpect( jsonPath( "cart.count", is( "one" ) )
               );
    }

    @Test
    public void shouldMergeEntityByJSR_RFC_Example() throws Exception
    {
        mockMvc.perform( patch( "/entity/v2/{id}", entityID2 ).contentType( MediaType.valueOf( PatchType.MERGE ) ).characterEncoding( "UTF-8" ).content( MERGE_PATCH ) )
               .andDo( print() )
               .andExpect( status().isOk() )
               .andExpect( result -> {
                   String content = result.getResponse().getContentAsString();
                   JSONAssert.assertEquals( MERGE_EXPECTED, content, false );
               } );
    }

}
