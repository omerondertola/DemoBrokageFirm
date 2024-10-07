
1. All endpoints are authorized with an admin user and password

        HAS ADMIN ROLE: Can edit all records
        Admin username: admin@gmail.com
        Admin password: admin
      
        HAS CUSTOMER ROLE: Can edit only own records
        Customer username: omerondertola@gmail.com
        Customer password: onder
        
        Customer username: defnetola@gmail.com
        Customer password: defne
        
        Customer username: iremtola@gmail.com
        Customer password: irem
      
        Passwords encrypted with BCrypt
        Http Session is stateful.

2. All info should be stored in database as below:

   Asset: id, customerId, assetName, size, usableSize
   Order: id, customerId, assetName, orderSide, size, price, status, createDate
   Customer: 

4. Authorization checks for Admins & Customers Performed.

   @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id ")

5. Matching end-point added.

6. Locking mechanism added to prevent consistency errors.

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId and a.assetName = :name")

7. Unit tests developed for services.

8. Integration tests developed for controllers.

9. List of End-Points:

    ORDERS:

        CREATE ORDER FOR CUSTOMER:
        {POST [/apis/v1/orders/{customerId}]} -> com.example.demo.orders.OrderController#createOrder(long, OrderDto)

        MATCH ORDER:
        {DELETE [/apis/v1/orders/match/{orderId}]} -> com.example.demo.orders.OrderController#matchOrder(long)

        QUERY ALL ORDERS:
        {GET [/apis/v1/orders]} -> com.example.demo.orders.OrderController#getAllOrders()

        CANCEL ORDER OF CUSTOMER:
        {DELETE [/apis/v1/orders/{customerId}/{orderId}]} -> com.example.demo.orders.OrderController#cancelOrder(long, long)

        GET ORDER OF CUSTOMER:
        {GET [/apis/v1/orders/{customerId}/{orderId}]} -> com.example.demo.orders.OrderController#getOrder(Long, Long)

        GET ALL ORDERS OF A CUSTOMER:
        {GET [/apis/v1/orders/{customerId}]} -> com.example.demo.orders.OrderController#getOrdersByDate(Long, Date, Date)

    ASSETS:

        DEPOSIT MONEY TO CUSTOMER:
        {POST [/apis/v1/assets/deposit/{customerId}]} -> com.example.demo.assets.AssetController#depositMoney(long, DepositMoneyDto, BindingResult)

        WITHSRAW MONEY FROM CUSTOMER:
        {POST [/apis/v1/assets/withdraw/{customerId}]} -> com.example.demo.assets.AssetController#withdrawMoney(long, WithdrawMoneyDto, BindingResult)

        QUERY ALL ASSETS OF CUSTOMER:
        {GET [/apis/v1/assets/{customerId}]} -> com.example.demo.assets.AssetController#getAssetsOfCustomer(long)

        QUERY ALL ASSETS
        {GET [/apis/v1/assets]} -> com.example.demo.assets.AssetController#getAllAssets()

    CUSTOMERS:

        QUERY CUSTOMER:
        {GET [/apis/v1/customers/{customerId}]} -> com.example.demo.customers.CustomerController#getCustomer(long)

        QUERY ALL CUSTOMERS:
        {GET [/apis/v1/customers]} -> com.example.demo.customers.CustomerController#getAllCustomers()

        CREATE A CUSTOMER:
        {POST [/apis/v1/customers]} -> com.example.demo.customers.CustomerController#createCustomer(Customer, BindingResult)      
      
      
11. Application is packaged as jar file and added to the repo as: demo-0.0.1.jar
    
12. To run the application execute java - jar demo-0.0.1.jar

13. Connection port is 8080

14. 4 Customers (1 Admin), 4 Assets, 2 Orders (Buy) Exists By Default.
    
   
