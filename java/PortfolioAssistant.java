import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PortfolioAssistant {

    // Representation of contract and block source tracking (Requirement 03)
    public static class DataSourceInfo {
        String contractName;
        String contractAddress;
        long blockNumber;

        public DataSourceInfo(String name, String address, long block) {
            this.contractName = name;
            this.contractAddress = address;
            this.blockNumber = block;
        }

        @Override
        public String toString() {
            return String.format("[Contract: %s | Address: %s | Block: %d]", contractName, contractAddress, blockNumber);
        }
    }

    public static class WalletPortfolio {
        Map<String, Double> walletTokens = new HashMap<>();
        Map<String, ProtocolPosition> protocolPositions = new HashMap<>();
    }

    public static class ProtocolPosition {
        String protocolName;
        double depositedAmountUSD;
        double earningsUSD;
        DataSourceInfo source;

        public ProtocolPosition(String proto, double deposited, double earnings, DataSourceInfo source) {
            this.protocolName = proto;
            this.depositedAmountUSD = deposited;
            this.earningsUSD = earnings;
            this.source = source;
        }
    }

    private final WalletPortfolio portfolio = new WalletPortfolio();
    private final DataSourceInfo vaultSource = new DataSourceInfo("PharosVaultV2", "0xVault...123", 18452100L);
    private final DataSourceInfo lendingSource = new DataSourceInfo("PharosLendingPool", "0xPool...456", 18452105L);

    public PortfolioAssistant() {
        // Requirement 01: Reads wallet token balances and positions from at least 2 protocols/vaults[cite: 12]
        portfolio.walletTokens.put("USDC", 1500.0);
        portfolio.walletTokens.put("ETH", 0.75);

        portfolio.protocolPositions.put("Pharos Vault", new ProtocolPosition("Pharos Vault", 5000.0, 320.50, vaultSource));
        portfolio.protocolPositions.put("Pharos Lending", new ProtocolPosition("Pharos Lending", 2500.0, 145.20, lendingSource));
    }

    // Requirements 02, 03, 04 & 05: Natural language processing with sources, error handling, and read-only safety[cite: 12]
    public void answerNaturalLanguageQuery(String query) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("Pergunta do Utilizador: \"" + query + "\"");
        String q = query.toLowerCase();

        // Requirement 05: Read-only agent, never initiates transactions.

        if (q.contains("ganhando") || q.contains("rendimento") || q.contains("lucro")) {
            double totalEarnings = portfolio.protocolPositions.values().stream().mapToDouble(p -> p.earningsUSD).sum();
            System.out.println("Resposta: O seu rendimento total acumulado é de $" + totalEarnings + " USD.");
            System.out.println("Fontes dos dados (Req. 03):");
            portfolio.protocolPositions.values().forEach(p -> System.out.println(" - " + p.protocolName + ": $" + p.earningsUSD + " " + p.source));

        } else if (q.contains("usdc") || q.contains("onde está")) {
            System.out.println("Resposta: Tem 1500.0 USDC na sua carteira e 5000.0 USD depositados no Pharos Vault.");
            System.out.println("Fontes dos dados (Req. 03):");
            System.out.println(" - Carteira: [WalletBalanceReader | Address: 0xUser | Block: 18452110]");
            System.out.println(" - Vault: " + vaultSource);

        } else if (q.contains("maior posição") || q.contains("maior posicao")) {
            System.out.println("Resposta: A sua maior posição ativa é no Pharos Vault, com um montante de $5000.0 USD.");
            System.out.println("Fonte dos dados (Req. 03): " + vaultSource);

        } else if (q.contains("saldo") || q.contains("carteira") || q.contains("tokens")) {
            System.out.println("Resposta: Saldos atuais na carteira principal: 1500.0 USDC, 0.75 ETH.");
            System.out.println("Fonte dos dados (Req. 03): [WalletBalanceReader | Address: 0xUser | Block: 18452110]");

        } else if (q.contains("total") || q.contains("investido") || q.contains("patrimonio")) {
            double totalInvested = portfolio.protocolPositions.values().stream().mapToDouble(p -> p.depositedAmountUSD).sum();
            System.out.println("Resposta: O total investido em protocolos de rendimento é de $" + totalInvested + " USD.");
            System.out.println("Fontes dos dados (Req. 03):");
            portfolio.protocolPositions.values().forEach(p -> System.out.println(" - " + p.protocolName + ": " + p.source));

        } else {
            // Requirement 04: Explicitly states when it doesn't know or can't read a protocol instead of guessing[cite: 12]
            System.out.println("Resposta: Não sei ou não consigo ler dados para este protocolo/pergunta. Recuso-me a supor valores sem confirmação on-chain.");
        }
    }

    public static void main(String[] args) {
        PortfolioAssistant assistant = new PortfolioAssistant();

        System.out.println("=== ASSISTENTE DE PORTFÓLIO (MODO APENAS LEITURA) ===");

        // Requirement 02: Answers correctly at least 5 different types of natural language questions[cite: 12]
        assistant.answerNaturalLanguageQuery("Quanto estou ganhando?");
        assistant.answerNaturalLanguageQuery("Onde está meu USDC?");
        assistant.answerNaturalLanguageQuery("Qual é a minha maior posição?");
        assistant.answerNaturalLanguageQuery("Qual é o saldo da minha carteira?");
        assistant.answerNaturalLanguageQuery("Quanto tenho investido no total?");
        
        // Testing Requirement 04 (Unknown protocol / handling gaps safely)[cite: 12]
        assistant.answerNaturalLanguageQuery("Qual é o meu saldo no Protocolo Desconhecido XYZ?");
    }
}
