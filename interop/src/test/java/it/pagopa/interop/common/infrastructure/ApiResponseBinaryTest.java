package it.pagopa.interop.common.infrastructure;

import io.restassured.response.Response;
import it.pagopa.infrastructure.response.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiResponseBinaryTest {
    @Test
    void binary_accessor_returns_defensive_copy_of_http_bytes() {
        Response response = mock(Response.class);
        byte[] body = new byte[]{0, (byte) 0xff, 10, 13, 0};
        when(response.getStatusCode()).thenReturn(200);
        when(response.asString()).thenReturn("");
        when(response.asByteArray()).thenReturn(body);
        ApiResponse apiResponse = new ApiResponse(response);

        byte[] firstCopy = apiResponse.asByteArray();
        firstCopy[1] = 0;

        assertThat(apiResponse.asByteArray()).containsExactly(0, (byte) 0xff, 10, 13, 0);
        assertThat(apiResponse.as(byte[].class)).containsExactly(0, (byte) 0xff, 10, 13, 0);
        verify(response, never()).as(byte[].class);
    }
}
