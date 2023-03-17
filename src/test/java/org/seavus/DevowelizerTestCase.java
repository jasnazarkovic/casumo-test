package org.seavus;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DevowelizerTestCase {

    private String endpoint;

    @BeforeTest
    public void setUp() {
        this.endpoint = "http://localhost:8080";
    }

    @Test(dataProvider = "testItDevowelizesLiteral")
    public void testItDevowelizesLiteralWithUrlEncoding(String input, String expected) throws URISyntaxException, MalformedURLException {
        URL url = new URL(String.format("%s/%s", this.endpoint, input));
        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());

        // NOTE: line below could make these tests pass, however,
        // that would be accommodating broken implementation in tests
        // instead of fixing underlying issue - service should return
        // "text/plain" as value for "Content-Type" header instead of
        // "text/html":
        //
        // RestAssured.registerParser("text/html", Parser.TEXT);

        RestAssured
                .get(uri.toASCIIString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(Matchers.equalTo(expected));
    }

    @DataProvider(name = "testItDevowelizesLiteral")
    public Object[][] getDataForTestItDevowelizesLiteral()
    {
        return new String[][] {
                {"0123456789", "0123456789"},
                {"aeiou", ""},
                {"bcdfghjklmnpqrstvwxyz", "bcdfghjklmnpqrstvwxyz"},
                {"abb", "bb"},
                {"bab", "bb"},
                {"bba", "bb"},
                {"ababa", "bb"},
                {"你跟我一起去吗", "你跟我一起去吗"},
                {"صباح","صباح"},
                {"abvgdđežzijklmnoprstćufhcčš","bvgdđžzjklmnprstćfhcčš"},
                // Bellow are failing test either because server does not
                // execute URL decoding, or because support for charset
                // is not implemented.
                {" ", " "},
                {"foo bar", "f br"},
                {"f!@#$%^&*()_+-=[]\\{}|;':\",./<>", "f!@#$%^&*()_+-=[]\\{}|;':\",./<>"},
                {"абвгдђежзијклљмнњопрстћуфхцчџш", "бвгдђжзјклљмнњпрстћфхцчџш"},
                {"ΑαΒβΓγΔδΕεΖζΗηΘθΙιΚκΛλΜμΝνΞξΟοΠπΡρΣσςΤτΥυΦφΧχΨψΩω", "ΒβΓγΔδΖζΘθΚκΛλΜμΝνΞξΠπΡρΣσςΤτΦφΧχΨψ"}
        };
    }

    @Test
    public void testItIgnoresQueryParams() {
        RestAssured
                .get(String.format("%s/foo?bar=baz", this.endpoint))
                .then()
                .statusCode(200)
                .assertThat()
                .body(Matchers.equalTo("f"));
    }

    /**
     * This test fails because response code is 200 and server
     * returns description on how to use service. This is not
     * described within documentation and current behaviour
     * should be considered as bug (400 HTTP bad request should
     * be expected - input is omitted, request is malformed).
     */
    @Test
    public void testItReturnsError400WhenInputIsOmitted() {
        RestAssured
                .get(this.endpoint)
                .then()
                .statusCode(400);
    }

    @Test()
    public void testItReturnsError404OnNonGetMethodRequests() {
        RestAssured
                .given()
                .body("")
                .contentType("text/plain")
                .when()
                .post(this.endpoint)
                .then()
                .statusCode(404);
    }
}
