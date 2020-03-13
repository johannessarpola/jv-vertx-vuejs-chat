package fi.bilot.commerce.types;

import fi.bilot.commerce.ProductsApiImpl;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.mockito.Mockito.*;

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
    ProductsApiImpl productsApiClient = new ProductsApiImpl(wsMock);


    when(wsMock.get(any(), any())).thenReturn(requestMock);
    when(requestMock.ssl(any())).thenReturn(requestMock);


    productsApiClient.listProducts(0,1);

  }


}