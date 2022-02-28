package net.johanbasson.rail;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResultTest {

    @Test
    public void shouldBeSuccess() {
        Result<String, Object> result = Result.success("John");
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isEqualTo(true);
        assertThat(result.isFailure()).isEqualTo(false);
    }

    @Test
    public void shouldBeFailure(){
        Result<Object, String> result = Result.failure("Error");
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isEqualTo(false);
        assertThat(result.isFailure()).isEqualTo(true);
    }

    @Test
    public void onSuccessShouldBeSuccessValue() {
        Result<String, Object> result = Result.success("John");
        assertThat(result).isNotNull();
        result.onSuccess(s -> assertThat(s).isEqualTo("John"));
    }

    @Test
    public void onFailureShouldBeFailureValue() {
        Result<String, Object> result = Result.failure("Error");
        assertThat(result).isNotNull();
        result.onFailure(s -> assertThat(s).isEqualTo("Error"));
    }

    @Test
    public void thenShouldOnlyProcessIfSuccess() {
        Result<String, Object> result = Result.success("John");
        assertThat(result).isNotNull();
        Result<String, Object> next = result.then(s -> Result.success(s + " Doe"));
        assertThat(next.isSuccess()).isEqualTo(true);
        next.onSuccess(val -> assertThat(val).isEqualTo("John Doe"));
    }

    @Test
    public void thenShouldNotProcessIfFailure() {
        Result<Object, String> result = Result.failure("Error");
        assertThat(result).isNotNull();
        Result<String, Object> next = result.then(s -> Result.success(s + " Message"));
        assertThat(next.isFailure()).isEqualTo(true);
        next.onFailure(val -> assertThat(val).isEqualTo("Error"));
    }

    @Test
    public void convertShouldOnlyProcessIfSuccess() {
        Result<String, Object> result = Result.success("John");
        Result<String, Object> next = result.convert(s -> s + " Doe");
        next.onSuccess(val -> assertThat(val).isEqualTo("John Doe"));
    }

}