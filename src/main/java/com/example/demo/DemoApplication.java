package com.example.demo;

import com.example.demo.assets.model.Asset;
import com.example.demo.assets.service.AssetService;
import com.example.demo.assets.service.NotEnoughAssetException;
import com.example.demo.customers.model.AppSecurityRoles;
import com.example.demo.customers.model.Customer;
import com.example.demo.customers.service.CustomerService;
import com.example.demo.orders.model.Order;
import com.example.demo.orders.service.OrderService;
import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.orders.model.OrderSide;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Date;

@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {

	private final CustomerService customerService;
	private final AssetService assetService;
	private final OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		createCustomers();
		createAssets();
		createOrders();

		printUserNames();
		printAPIEndPoints();
	}

	private void printAPIEndPoints() {
		System.out.println("");
		System.out.println("API END POINTS");
		System.out.println("====================================");
	}

	private void printUserNames() {
		System.out.println("ROLES: Usernames & Passwords Follows");
		System.out.println("====================================");
		System.out.println("ADMIN: admin@gmail.com/admin");
		System.out.println("CUSTOMER: omerondertola@gmail.com/onder");
		System.out.println("CUSTOMER: defnetola@gmail.com/defne");
		System.out.println("CUSTOMER: iremtola@gmail.com/irem");
	}

	private void createOrders() throws AssetNotFoundException, NotEnoughMoneyException, NotEnoughAssetException {
		Order orderBuyKCHol = Order.builder()
				.assetName("KCHOL")
				.createDate(new Date(System.currentTimeMillis()))
				.price(1.733)
				.orderSide(OrderSide.BUY)
				.size(10)
				.customerId(1L)
				.build();
		orderService.createOrder(orderBuyKCHol);

		Order orderBuySASA = Order.builder()
				.assetName("SASA")
				.createDate(new Date(System.currentTimeMillis()))
				.price(1)
				.orderSide(OrderSide.BUY)
				.size(100)
				.customerId(1L)
				.build();
		orderService.createOrder(orderBuySASA);
	}

	private void createAssets() {
		Asset omerTRY = Asset.builder()
				.assetName("TRY")
				.size(10_000)
				.usableSize(10_000)
				.customerId(1L)
				.build();
		assetService.create(omerTRY);

		Asset omerISYAT = Asset.builder()
				.assetName("ISYAT")
				.size(10_000)
				.usableSize(10_000)
				.customerId(1L)
				.build();
		assetService.create(omerISYAT);

		Asset defneTRY = Asset.builder()
				.assetName("TRY")
				.size(5_000)
				.usableSize(5_000)
				.customerId(2L)
				.build();
		assetService.create(defneTRY);

		Asset iremTRY = Asset.builder()
				.assetName("TRY")
				.size(5_000)
				.usableSize(5_000)
				.customerId(3L)
				.build();
		assetService.create(iremTRY);
	}

	private void createCustomers() {
		Customer omer = Customer.builder()
				.email("omerondertola@gmail.com")
				.firstName("Ömer Önder")
				.lastName("Tola")
				.password("onder")
				.role(AppSecurityRoles.CUSTOMER)
				.build();
		customerService.create(omer);

		Customer defne = Customer.builder()
				.email("defnetola@gmail.com")
				.firstName("Defne")
				.lastName("Tola")
				.password("defne")
				.role(AppSecurityRoles.CUSTOMER)
				.build();
		customerService.create(defne);

		Customer irem = Customer.builder()
				.email("iremtola@gmail.com")
				.firstName("İrem")
				.lastName("Tola")
				.password("irem")
				.role(AppSecurityRoles.CUSTOMER)
				.build();
		customerService.create(irem);

		// Let's define admin as a customer
		// This can be performed in various ways,
		// for demons
		Customer admin = Customer.builder()
				.email("admin@gmail.com")
				.firstName("Ömer Önder")
				.lastName("Tola")
				.password("admin")
				.role(AppSecurityRoles.ADMIN)
				.build();
		customerService.create(admin);

	}
}
