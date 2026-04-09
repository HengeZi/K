package com.finance.app.ui;

import com.finance.app.model.*;
import com.finance.app.service.FinanceService;

import java.util.List;
import java.util.Scanner;

/**
 * Main UI class for the Finance Record App
 * Provides a simple and beautiful console interface
 */
public class MainApp {
    private FinanceService financeService;
    private Scanner scanner;
    private User currentUser;
    private boolean running;
    
    // ANSI color codes for beautiful output
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE_BOLD = "\u001B[1m\u001B[37m";
    private static final String GREEN_BOLD = "\u001B[1m\u001B[32m";
    private static final String CYAN_BOLD = "\u001B[1m\u001B[36m";
    
    public MainApp() {
        this.financeService = new FinanceService();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public void start() {
        showWelcomeScreen();
        
        while (running) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
        
        System.out.println(GREEN_BOLD + "\n感谢使用理财记录应用！再见！" + RESET);
        scanner.close();
    }
    
    private void showWelcomeScreen() {
        clearScreen();
        System.out.println(CYAN_BOLD);
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║                                                        ║");
        System.out.println("║           🌟  理财记录管理系统  🌟                      ║");
        System.out.println("║                                                        ║");
        System.out.println("║        Finance Record Management System                ║");
        System.out.println("║                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
        pause(1000);
    }
    
    private void showAuthMenu() {
        System.out.println("\n" + WHITE_BOLD + "═══ 用户认证 ═══" + RESET);
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 退出应用");
        System.out.print("\n请选择操作 (1-3): ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                running = false;
                break;
            default:
                System.out.println(RED + "无效的选择，请重新输入。" + RESET);
                pause(1500);
        }
    }
    
    private void showMainMenu() {
        clearScreen();
        System.out.println(CYAN_BOLD + "欢迎回来，" + currentUser.getFullName() + "!" + RESET);
        System.out.println(WHITE_BOLD + "\n═══ 主菜单 ═══" + RESET);
        System.out.println("1. 查看理财产品");
        System.out.println("2. 我的持仓");
        System.out.println("3. 交易记录");
        System.out.println("4. 买入产品");
        System.out.println("5. 卖出产品");
        System.out.println("6. 个人信息");
        System.out.println("7. 修改密码");
        System.out.println("8. 退出登录");
        System.out.println("0. 退出应用");
        System.out.print("\n请选择操作 (0-8): ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "0":
                running = false;
                break;
            case "1":
                viewProducts();
                break;
            case "2":
                viewHoldings();
                break;
            case "3":
                viewTransactions();
                break;
            case "4":
                buyProduct();
                break;
            case "5":
                sellProduct();
                break;
            case "6":
                viewProfile();
                break;
            case "7":
                changePassword();
                break;
            case "8":
                logout();
                break;
            default:
                System.out.println(RED + "无效的选择，请重新输入。" + RESET);
                pause(1500);
        }
    }
    
    private void register() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 用户注册 ═══" + RESET);
        
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("请输入密码 (至少6位): ");
        String password = scanner.nextLine().trim();
        
        System.out.print("请输入姓名: ");
        String fullName = scanner.nextLine().trim();
        
        if (financeService.register(username, password, fullName)) {
            System.out.println(GREEN + "\n✓ 注册成功！请登录。" + RESET);
        } else {
            System.out.println(RED + "\n✗ 注册失败：用户名已存在或信息不完整。" + RESET);
        }
        pause(2000);
    }
    
    private void login() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 用户登录 ═══" + RESET);
        
        System.out.print("用户名: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("密码: ");
        String password = scanner.nextLine().trim();
        
        User user = financeService.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println(GREEN + "\n✓ 登录成功！" + RESET);
        } else {
            System.out.println(RED + "\n✗ 登录失败：用户名或密码错误。" + RESET);
        }
        pause(1500);
    }
    
    private void logout() {
        currentUser = null;
        System.out.println(GREEN + "\n✓ 已退出登录。" + RESET);
        pause(1000);
    }
    
    private void viewProducts() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 理财产品列表 ═══" + RESET);
        
        List<FinancialProduct> products = financeService.getAllProducts();
        
        System.out.printf("%-8s %-15s %-8s %-10s %-12s %-8s%n", 
                "产品ID", "产品名称", "类型", "单价(¥)", "预期收益%", "风险等级");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        for (FinancialProduct product : products) {
            String riskColor = getRiskColor(product.getRiskLevel());
            System.out.printf("%-8s %-15s %-8s %-10.2f %-12.2f %s%-8s%s%n",
                    product.getProductId(),
                    truncate(product.getProductName(), 15),
                    getProductTypeShort(product.getProductType()),
                    product.getUnitPrice(),
                    product.getExpectedReturnRate(),
                    riskColor,
                    product.getRiskLevel(),
                    RESET);
        }
        
        System.out.println("\n按回车键返回...");
        scanner.nextLine();
    }
    
    private void viewHoldings() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 我的持仓 ═══" + RESET);
        
        List<Holding> holdings = financeService.getUserHoldings(currentUser.getUsername());
        
        if (holdings.isEmpty()) {
            System.out.println(YELLOW + "暂无持仓产品。" + RESET);
        } else {
            double totalValue = financeService.getTotalHoldingsValue(currentUser.getUsername());
            System.out.println(GREEN_BOLD + "总资产价值：¥" + String.format("%.2f", totalValue) + RESET);
            System.out.println();
            
            System.out.printf("%-8s %-15s %-10s %-12s %-12s %-12s %-12s%n",
                    "产品ID", "产品名称", "持有份额", "平均成本", "当前价值", "总成本", "盈亏");
            System.out.println("─────────────────────────────────────────────────────────────────────");
            
            for (Holding holding : holdings) {
                String plColor = holding.getProfitLoss() >= 0 ? GREEN : RED;
                System.out.printf("%-8s %-15s %-10.2f %-12.2f %-12.2f %-12.2f %s%-12.2f%s%n",
                        holding.getProductId(),
                        truncate(holding.getProductName(), 15),
                        holding.getQuantity(),
                        holding.getAverageCost(),
                        holding.getCurrentValue(),
                        holding.getTotalCost(),
                        plColor,
                        holding.getProfitLoss(),
                        RESET);
            }
        }
        
        System.out.println("\n按回车键返回...");
        scanner.nextLine();
    }
    
    private void viewTransactions() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 交易记录 ═══" + RESET);
        
        List<Transaction> transactions = financeService.getUserTransactions(currentUser.getUsername());
        
        if (transactions.isEmpty()) {
            System.out.println(YELLOW + "暂无交易记录。" + RESET);
        } else {
            System.out.printf("%-12s %-6s %-8s %-15s %-8s %-10s %-12s%n",
                    "时间", "类型", "产品ID", "产品名称", "份额", "单价(¥)", "总金额(¥)");
            System.out.println("─────────────────────────────────────────────────────────────────────────");
            
            for (Transaction t : transactions) {
                String typeColor = t.getType().equals("BUY") ? GREEN : RED;
                System.out.printf("%-12s %s%-6s%s %-8s %-15s %-8.2f %-10.2f %-12.2f%n",
                        t.getFormattedDate().substring(5, 16),
                        typeColor,
                        t.getType(),
                        RESET,
                        t.getProductId(),
                        truncate(t.getProductName(), 15),
                        t.getQuantity(),
                        t.getUnitPrice(),
                        t.getTotalAmount());
            }
        }
        
        System.out.println("\n按回车键返回...");
        scanner.nextLine();
    }
    
    private void buyProduct() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 买入产品 ═══" + RESET);
        
        System.out.print("请输入产品ID: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        
        FinancialProduct product = financeService.getProductById(productId);
        if (product == null) {
            System.out.println(RED + "✗ 产品不存在。" + RESET);
            pause(1500);
            return;
        }
        
        System.out.println("产品信息：" + product.getProductName() + 
                          " | 单价：¥" + product.getUnitPrice() +
                          " | 风险等级：" + product.getRiskLevel());
        
        System.out.print("请输入买入份额: ");
        try {
            double quantity = Double.parseDouble(scanner.nextLine().trim());
            if (quantity <= 0) {
                System.out.println(RED + "✗ 份额必须大于0。" + RESET);
                pause(1500);
                return;
            }
            
            double totalAmount = quantity * product.getUnitPrice();
            System.out.println(YELLOW + "预计花费：¥" + String.format("%.2f", totalAmount) + RESET);
            System.out.print("确认买入？(y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y")) {
                Transaction transaction = financeService.buyProduct(currentUser.getUsername(), productId, quantity);
                if (transaction != null) {
                    System.out.println(GREEN + "\n✓ 买入成功！" + RESET);
                    System.out.println("交易ID: " + transaction.getTransactionId());
                } else {
                    System.out.println(RED + "\n✗ 买入失败。" + RESET);
                }
            } else {
                System.out.println(YELLOW + "已取消操作。" + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "✗ 请输入有效的数字。" + RESET);
        }
        
        pause(2000);
    }
    
    private void sellProduct() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 卖出产品 ═══" + RESET);
        
        // Show holdings first
        List<Holding> holdings = financeService.getUserHoldings(currentUser.getUsername());
        if (holdings.isEmpty()) {
            System.out.println(YELLOW + "暂无持仓产品，无法卖出。" + RESET);
            pause(1500);
            return;
        }
        
        System.out.println("当前持仓：");
        for (Holding h : holdings) {
            System.out.printf("  %s - %s: %.2f 份额%n", h.getProductId(), h.getProductName(), h.getQuantity());
        }
        
        System.out.print("\n请输入要卖出的产品ID: ");
        String productId = scanner.nextLine().trim().toUpperCase();
        
        Holding holding = financeService.getUserHoldings(currentUser.getUsername()).stream()
                .filter(h -> h.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
                
        if (holding == null) {
            System.out.println(RED + "✗ 您没有该产品的持仓。" + RESET);
            pause(1500);
            return;
        }
        
        FinancialProduct product = financeService.getProductById(productId);
        System.out.println("产品信息：" + holding.getProductName() + 
                          " | 当前单价：¥" + product.getUnitPrice() +
                          " | 可用份额：" + holding.getQuantity());
        
        System.out.print("请输入卖出份额: ");
        try {
            double quantity = Double.parseDouble(scanner.nextLine().trim());
            if (quantity <= 0 || quantity > holding.getQuantity()) {
                System.out.println(RED + "✗ 份额无效或超出持有数量。" + RESET);
                pause(1500);
                return;
            }
            
            double totalAmount = quantity * product.getUnitPrice();
            System.out.println(GREEN + "预计获得：¥" + String.format("%.2f", totalAmount) + RESET);
            System.out.print("确认卖出？(y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y")) {
                Transaction transaction = financeService.sellProduct(currentUser.getUsername(), productId, quantity);
                if (transaction != null) {
                    System.out.println(GREEN + "\n✓ 卖出成功！" + RESET);
                    System.out.println("交易ID: " + transaction.getTransactionId());
                } else {
                    System.out.println(RED + "\n✗ 卖出失败。" + RESET);
                }
            } else {
                System.out.println(YELLOW + "已取消操作。" + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "✗ 请输入有效的数字。" + RESET);
        }
        
        pause(2000);
    }
    
    private void viewProfile() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 个人信息 ═══" + RESET);
        
        User user = financeService.getUser(currentUser.getUsername());
        if (user != null) {
            System.out.println("用户名: " + user.getUsername());
            System.out.println("姓名: " + user.getFullName());
            System.out.println("邮箱: " + (user.getEmail() != null ? user.getEmail() : "未设置"));
            System.out.println("电话: " + (user.getPhone() != null ? user.getPhone() : "未设置"));
            
            System.out.println("\n是否修改个人信息？(y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            
            if (choice.equals("y")) {
                System.out.print("请输入新姓名: ");
                String fullName = scanner.nextLine().trim();
                if (!fullName.isEmpty()) {
                    user.setFullName(fullName);
                }
                
                System.out.print("请输入邮箱 (留空跳过): ");
                String email = scanner.nextLine().trim();
                if (!email.isEmpty()) {
                    user.setEmail(email);
                }
                
                System.out.print("请输入电话 (留空跳过): ");
                String phone = scanner.nextLine().trim();
                if (!phone.isEmpty()) {
                    user.setPhone(phone);
                }
                
                if (financeService.updateUser(user)) {
                    currentUser = user;
                    System.out.println(GREEN + "\n✓ 信息更新成功！" + RESET);
                } else {
                    System.out.println(RED + "\n✗ 信息更新失败。" + RESET);
                }
            }
        }
        
        pause(2000);
    }
    
    private void changePassword() {
        clearScreen();
        System.out.println(WHITE_BOLD + "\n═══ 修改密码 ═══" + RESET);
        
        System.out.print("请输入旧密码: ");
        String oldPassword = scanner.nextLine().trim();
        
        System.out.print("请输入新密码 (至少6位): ");
        String newPassword = scanner.nextLine().trim();
        
        System.out.print("请再次输入新密码: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println(RED + "\n✗ 两次输入的密码不一致。" + RESET);
            pause(1500);
            return;
        }
        
        if (financeService.changePassword(currentUser.getUsername(), oldPassword, newPassword)) {
            System.out.println(GREEN + "\n✓ 密码修改成功！" + RESET);
        } else {
            System.out.println(RED + "\n✗ 密码修改失败：旧密码错误或新密码不符合要求。" + RESET);
        }
        
        pause(2000);
    }
    
    // Helper methods
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
    
    private String truncate(String str, int length) {
        if (str == null) return "";
        if (str.length() <= length) return str;
        return str.substring(0, length - 2) + "..";
    }
    
    private String getProductTypeShort(String type) {
        if (type == null) return "";
        switch (type.toLowerCase()) {
            case "fund": return "基金";
            case "stock": return "股票";
            case "bond": return "债券";
            case "insurance": return "保险";
            default: return type;
        }
    }
    
    private String getRiskColor(String riskLevel) {
        if (riskLevel == null) return RESET;
        switch (riskLevel.toLowerCase()) {
            case "low": return GREEN;
            case "medium": return YELLOW;
            case "high": return RED;
            default: return RESET;
        }
    }
    
    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.start();
    }
}
