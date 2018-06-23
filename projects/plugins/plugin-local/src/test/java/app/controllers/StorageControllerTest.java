package app.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import app.client.StorageApi;
import app.common.FileUtils;
import app.common.UploadFileResponse;
import app.models.Body;
import app.models.PluginStorageFileUploadModel;
import app.models.PluginStorageModel;
import utils.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StorageControllerTest {

    @Autowired
    private StorageController controller;
    @Autowired
    private TestRestTemplate restTemplate;
    @Value("${local.server.port}")
    private int PORT;
    private static String LOCATION = "fake_location";
    private static String SPACE_NAME = "bionimbuz_local_space";
    private static String TEST_FILE_NAME = "test_file.txt";
    private static String TEST_FILE_PATH = "src/test/resources/"+TEST_FILE_NAME;

    @Before
    public void init() {
        File file = new File("spaces/" + SPACE_NAME);
        FileUtils.deleteDir(file);
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void CRUD_Test() throws IOException {
        deleteNonExistentSpaceTest();
        createSpaceTest();
        uploadFileTest();
        deleteExistentSpaceTest();
    }


    private void createSpaceTest() throws IOException {
        PluginStorageModel model = createModel();
        StorageApi api = new StorageApi(TestUtils.getUrl(PORT));

        Body<PluginStorageModel> body = api.createSpace("", "", model);
        assertThat(body).isNotNull();
        assertThat(body.getContent().getName()).isEqualTo(SPACE_NAME);
    }

    private PluginStorageModel createModel() {
        PluginStorageModel model = new PluginStorageModel(
                SPACE_NAME,
                LOCATION);
        return model;
    }

    public void deleteNonExistentSpaceTest() throws IOException {

        StorageApi api = new StorageApi(TestUtils.getUrl(PORT));

        Body<Boolean> body = api.deleteSpace("", "", SPACE_NAME);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isFalse();
    }

    public void deleteExistentSpaceTest() throws IOException {

        StorageApi api = new StorageApi(TestUtils.getUrl(PORT));

        Body<Boolean> body = api.deleteSpace("", "", SPACE_NAME);
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isTrue();
    }

    public void uploadFileTest() throws IOException {

        StorageApi api = new StorageApi(TestUtils.getUrl(PORT));
        Body<PluginStorageFileUploadModel> body =
                api.getUploadUrl(SPACE_NAME, TEST_FILE_NAME);
        assertThat(body).isNotNull();

        PluginStorageFileUploadModel content =
                body.getContent();

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        File file = new File(TEST_FILE_PATH);
        map.add("file", new FileSystemResource(file));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity =
                new HttpEntity<LinkedMultiValueMap<String, Object>>(
                            map, headers);
        ResponseEntity<UploadFileResponse> response = this.restTemplate
                .exchange(
                        content.getUrl(),
                        HttpMethod.valueOf(content.getMethod()),
                        entity,
                        new ParameterizedTypeReference< UploadFileResponse >() {});

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
