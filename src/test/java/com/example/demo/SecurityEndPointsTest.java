package com.example.demo;

import com.example.demo.assets.AssetController;
import com.example.demo.customers.CustomerController;
import com.example.demo.orders.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityEndPointsTest {

    @Autowired
    private CustomerController customerController;

    @Autowired
    private OrderController orderController;

    @Autowired
    private AssetController assetController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testContextLoaded() {
    }

    @Test
    public void testCustomerControllerShallBeWired() {
        assertThat(customerController).isNotNull();
        assertThat(assetController).isNotNull();
        assertThat(orderController).isNotNull();
    }

    @Test
    public void testCustomersApiIsProtected() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/apis/v1/customers/1", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testCustomersApiIsProtected2() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                "/apis/v1/customers", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testAssetsApiIsProtected() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/apis/v1/assets", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testAssetsApiIsProtected2() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/apis/v1/assets/1", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testOrdersApiIsProtected() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/apis/v1/orders", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testOrdersApiIsProtected2() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/apis/v1/orders/1", String.class);
        assertThat(forObject).contains("""
                        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
                      </form>
                """);
    }

    @Test
    public void testLoginApiIsAvailable() {
        String forObject = this.restTemplate.getForObject("http://localhost:" + port +
                        "/login", String.class);
        assertThat(forObject).contains("""
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Please sign in</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" integrity="sha384-oOE/3m0LUMPub4kaC09mrdEhIc+e3exm4xOGxAmuFXhBNF4hcg/6MiAXAf5p0P56" crossorigin="anonymous"/>
  </head>
  <body>
     <div class="container">
      <form class="form-signin" method="post" action="/login">
        <h2 class="form-signin-heading">Please sign in</h2>
        <p>
          <label for="username" class="sr-only">Username</label>
          <input type="text" id="username" name="username" class="form-control" placeholder="Username" required autofocus>
        </p>
        <p>
          <label for="password" class="sr-only">Password</label>
          <input type="password" id="password" name="password" class="form-control" placeholder="Password" required>
        </p>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
      </form>
</div>
</body></html>""");
    }

}