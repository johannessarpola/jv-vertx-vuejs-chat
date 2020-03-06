package fi.bilot.commerce.types;

import fi.bilot.commerce.ProductsApiClient;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class ProductsApiClientTest {





  @Test
  public void testParsing() throws URISyntaxException, IOException {
    URI resource = getClass().getClassLoader().getResource("product_search_response.json").toURI();
    String json = Files.readString(Paths.get(resource));

    WebClient wsMock = mock(WebClient.class);
    HttpRequest requestMock = mock(HttpRequest.class);
    HttpResponse<Buffer> responseMock = mock(HttpResponse.class);
    ProductsApiClient productsApiClient = new ProductsApiClient(wsMock);


    when(wsMock.get(any(), any())).thenReturn(requestMock);
    when(requestMock.ssl(any())).thenReturn(requestMock);


    productsApiClient.listProducts(0,1);

  }


}