# Crypto Exchange Platform

A comprehensive cryptocurrency exchange platform built with Spring Boot, featuring Ethereum blockchain integration, user management, and transaction processing capabilities.

## 🚀 Features

### Core Functionality
- **User Authentication & Authorization** - Secure login with role-based access control
- **Cryptocurrency Deposits** - Process ETH deposits with blockchain verification
- **Withdrawals** - Both synchronous and asynchronous withdrawal processing
- **Internal Transfers** - Transfer crypto between users within the platform
- **Ethereum Integration** - Real-time blockchain data and transaction processing
- **Transaction Logging** - Comprehensive audit trail for all operations
- **Rate Limiting** - API rate limiting to prevent abuse

### Technical Features
- **Spring Boot 3.4.5** with Java 21
- **PostgreSQL Database** with JPA/Hibernate
- **Web3j Integration** for Ethereum blockchain interaction
- **Spring Security** with Basic Authentication
- **Async Processing** for long-running operations
- **Comprehensive Logging** with performance monitoring
- **Docker Support** for easy deployment
- **TestContainers** for integration testing

## 📋 Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**
- **PostgreSQL 14** (or use Docker)
- **Infura API Key** (for Ethereum mainnet access)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd crypto-exchange
```

### 2. Environment Configuration

Create an environment variable for your Infura API key:
```bash
export INFURA_PROJECT_ID=your_infura_project_id_here
```

### 3. Database Setup

Start PostgreSQL using Docker Compose:
```bash
cd crypto-exchange
docker-compose up -d
```

The database will be available at:
- **Host**: localhost
- **Port**: 5433
- **Database**: CryptoEXchangeDB
- **Username**: nikgeo
- **Password**: 1234

### 4. Initialize Test Data

Run the SQL scripts to set up initial data:
```bash
# Add test users
docker exec -i crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB < add_user2.sql

# Set up initial balances
docker exec -i crypto_exchange_postgres psql -U nikgeo -d CryptoEXchangeDB < setup_test_data.sql
```

### 5. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on **http://localhost:8081**

## 🔐 Authentication

The application uses Basic Authentication with the following default credentials:
- **Username**: nikgeo
- **Password**: password

Include the Authorization header in all API requests:
```
Authorization: Basic bmlrZ2VvOnBhc3N3b3Jk
```

## 📚 API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login

### Ethereum Blockchain
- `GET /api/v1/ethereum/block-number` - Get current Ethereum block number
- `GET /block` - Alternative block number endpoint

### Deposits
- `POST /api/v1/deposit` - Process cryptocurrency deposits

**Request Body:**
```json
{
    "userId": 1,
    "tokenSymbol": "ETH",
    "amount": 2.5,
    "walletAddress": "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
}
```

### Withdrawals
- `POST /api/v1/withdraw` - Asynchronous withdrawal processing
- `POST /api/v1/withdraw/sync` - Synchronous withdrawal processing
- `POST /api/v1/withdraw/test` - Test withdrawal endpoint

**Request Body:**
```json
{
    "userId": 1,
    "tokenSymbol": "ETH",
    "amount": 0.5,
    "toAddress": "0x742d35Cc6634C0532925a3b844Bc454e4438f44e"
}
```

### Transfers
- `POST /api/v1/transfer` - Transfer crypto between users

**Request Body:**
```json
{
    "fromUserId": 1,
    "toUserId": 2,
    "tokenSymbol": "ETH",
    "amount": 1.0
}
```

## 🧪 Testing

### Automated Testing Script

Run the comprehensive PowerShell testing script:
```powershell
.\test_endpoints.ps1
```

This script will:
1. Test user authentication
2. Verify Ethereum blockchain connectivity
3. Process deposits and verify balance updates
4. Test withdrawals (both sync and async)
5. Perform internal transfers between users
6. Display all transaction history

### Manual Testing Commands

Individual test commands are available in `tetsPScommands.txt` for manual testing.

### Database Queries

Monitor transaction history and balances:
```sql
-- View all transactions
SELECT 
    tl.log_id,        
    tl.transaction_type,
    u.name as user_name,
    t.symbol as token_symbol,
    tl.amount,          
    tl.status,
    tl.tx_hash,
    tl.created_at
FROM transaction_logs tl 
JOIN users u ON tl.user_id = u.user_id 
JOIN tokens t ON tl.token_id = t.token_id 
ORDER BY tl.created_at DESC;

-- Check user balances
SELECT 
    u.email,
    t.symbol,
    ub.amount,
    ub.updated_at
FROM balances ub
JOIN users u ON ub.user_id = u.user_id
JOIN tokens t ON ub.token_id = t.token_id
WHERE u.user_id IN (1, 2)
ORDER BY u.user_id;
```

## 🏗️ Architecture

### Project Structure
```
crypto-exchange/
├── src/main/java/com/example/crypto_exchange/
│   ├── controller/          # REST API controllers
│   ├── service/            # Business logic services
│   ├── repository/         # Data access layer
│   ├── entity/            # JPA entities
│   ├── dto/               # Data transfer objects
│   ├── config/            # Configuration classes
│   ├── exception/         # Custom exceptions
│   └── Web3Config.java    # Web3j configuration
├── src/main/resources/
│   └── application.properties
├── src/test/              # Test classes
├── docs/                  # Documentation
├── docker-compose.yml     # Database setup
└── pom.xml               # Maven configuration
```

### Key Components

#### Controllers
- **AuthController** - Handles user authentication
- **EthereumController** - Ethereum blockchain operations
- **DepositController** - Cryptocurrency deposits
- **WithdrawController** - Withdrawal processing with rate limiting
- **TransferController** - Internal user transfers

#### Services
- **BlockchainService** - Ethereum blockchain integration
- **DepositService** - Deposit processing logic
- **WithdrawService** - Withdrawal processing with validation
- **TransferService** - Internal transfer operations
- **UserBalanceService** - Balance management
- **TransactionLogService** - Transaction history and logging
- **DepositWatcherService** - Automated deposit monitoring

#### Configuration
- **SecurityConfig** - Spring Security configuration
- **GlobalExceptionHandler** - Centralized error handling
- **PerformanceLoggingAspect** - Method performance monitoring
- **AsyncConfig** - Asynchronous processing configuration

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Infura Configuration
infura.project.id=${INFURA_PROJECT_ID}
infura.api.url=https://mainnet.infura.io/v3/f77ecfe31d0a49078dd1a3dcd0f853b9

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5433/CryptoEXchangeDB
spring.datasource.username=nikgeo
spring.datasource.password=1234

# Server Configuration
server.port=8081

# Deposit Watcher
deposit.watcher.fixed-delay-ms=60000

# Logging Configuration
logging.level.com.example.crypto_exchange=DEBUG
logging.file.name=logs/application.log
```

### Rate Limiting

Withdrawal endpoints are protected with rate limiting:
- **10 requests per minute** per user
- Configurable in `WithdrawController`

## 📊 Database Schema

### Core Tables
- **users** - User accounts and information
- **tokens** - Supported cryptocurrencies
- **balances** - User token balances
- **transaction_logs** - Complete transaction history

### Key Relationships
- Users can have multiple token balances
- All transactions are logged with status tracking
- Support for both ETH and ERC-20 tokens

## 🚨 Error Handling

The application includes comprehensive error handling:

- **WithdrawException** - Withdrawal-specific errors
- **InvalidInputException** - Input validation errors
- **GlobalExceptionHandler** - Centralized error responses
- **Rate limiting** - Protection against API abuse

## 🔒 Security Features

- **Spring Security** with Basic Authentication
- **Input validation** using Bean Validation
- **Rate limiting** on sensitive endpoints
- **Comprehensive logging** for audit trails
- **Transaction isolation** for data consistency

## 📈 Monitoring & Logging

### Logging Features
- **Structured logging** with SLF4J
- **Performance monitoring** with AOP
- **Repository query logging**
- **File rotation** with size limits
- **Debug-level logging** for development

### Performance Monitoring
- **Method execution time** tracking
- **Slow method detection** (>1 second)
- **Database query performance** monitoring

## 🐳 Docker Support

### Database Container
```bash
# Start database
docker-compose up -d

# Stop database
docker-compose down

# View logs
docker-compose logs db
```

### Application Container (Future)
The application can be containerized for production deployment.

## 🧪 Testing Strategy

### Test Types
- **Unit Tests** - Individual component testing
- **Integration Tests** - API endpoint testing
- **TestContainers** - Database integration testing

### Test Coverage
- **Service layer** - Business logic validation
- **Controller layer** - API endpoint testing
- **Repository layer** - Data access testing

## 🔄 Development Workflow

1. **Setup Environment** - Install prerequisites
2. **Start Database** - Use Docker Compose
3. **Configure API Keys** - Set Infura project ID
4. **Run Application** - Start Spring Boot app
5. **Execute Tests** - Run automated test script
6. **Monitor Logs** - Check application logs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📝 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions:
1. Check the logs in `logs/application.log`
2. Review the test script output
3. Verify database connectivity
4. Ensure Infura API key is configured

## 🔮 Future Enhancements

- **Multi-chain support** (Bitcoin, Polygon, etc.)
- **Advanced order types** (limit orders, stop-loss)
- **Real-time price feeds**
- **Mobile API endpoints**
- **WebSocket support** for real-time updates
- **Advanced security** (JWT tokens, 2FA)
- **Admin dashboard** for platform management
