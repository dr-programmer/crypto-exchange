# Blockchain Service Documentation

## Overview
The `BlockchainService` is a Spring service that provides a high-level interface for interacting with the Ethereum blockchain. It encapsulates common blockchain operations like checking balances and preparing transactions, making it easy to integrate Ethereum functionality into the application.

## Features
- ETH balance checking
- ERC-20 token balance checking
- Transaction preparation (stub for future implementation)

## Dependencies
- Spring Framework
- Web3j
- Java 8+

## Configuration
The service requires an Infura API key to be configured in `application.properties`:
```properties
infura.project.id=${INFURA_PROJECT_ID}
infura.api.url=https://mainnet.infura.io/v3/YOUR_PROJECT_ID
```

## Usage

### Dependency Injection
```java
@Autowired
private BlockchainService blockchainService;
```

### Checking ETH Balance
```java
String address = "0x123..."; // Ethereum address
BigDecimal balance = blockchainService.getEthBalance(address);
// balance will be in ETH (e.g., 1.5 ETH)
```

### Checking ERC-20 Token Balance
```java
String tokenAddress = "0x456..."; // ERC-20 token contract address
String walletAddress = "0x789..."; // Wallet address to check
int decimals = 18; // Token decimals (most tokens use 18)

BigDecimal tokenBalance = blockchainService.getTokenBalance(
    tokenAddress,
    walletAddress,
    decimals
);
// tokenBalance will be in token units (e.g., 100.0 tokens)
```

### Error Handling
The service throws `RuntimeException` with descriptive messages when operations fail:
- Invalid addresses
- Network connectivity issues
- Contract interaction failures

Example error handling:
```java
try {
    BigDecimal balance = blockchainService.getEthBalance(address);
} catch (RuntimeException e) {
    // Handle error appropriately
    logger.error("Failed to get balance: " + e.getMessage());
}
```

## Future Enhancements
1. Transaction sending implementation
2. Gas price estimation
3. Transaction status monitoring
4. Event listening for smart contracts
5. Support for multiple networks (testnet, private networks)

## Best Practices
1. Always validate addresses before calling service methods
2. Handle exceptions appropriately in your application code
3. Consider implementing retry logic for network operations
4. Cache balance results when appropriate
5. Monitor gas prices for transaction operations

## Security Considerations
1. Never expose private keys in the service
2. Validate all input addresses
3. Implement rate limiting for public endpoints
4. Consider implementing request signing for sensitive operations
5. Monitor for suspicious activity

## Contributing
When adding new features to the service:
1. Follow the existing code style
2. Add comprehensive JavaDoc documentation
3. Include unit tests
4. Update this documentation
5. Consider backward compatibility 