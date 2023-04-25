import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class StockClient extends UnicastRemoteObject implements StockInterface {
    private static final long serialVersionUID = 1L;
    private StockInterface server;
    private List<Stock> stockList = new ArrayList<>();
    
    public StockClient(StockInterface server) throws RemoteException {
        this.server = server;
        server.registerClient(this);
        stockList = server.getStockList();
    }
   
    public void notifyStockChanged() throws RemoteException {
        stockList = server.getStockList();
        System.out.println("Stock changed:");
        for (Stock stock : stockList) {
            System.out.println(stock.getName() + ": " + stock.getQuantity());
        }
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public int getStockQuantity(String name) {
        for (Stock stock : stockList) {
            if (stock.getName().equals(name)) {
                return stock.getQuantity();
            }
        }
        return 0;
    }

    public void addStock(String name, int quantity) throws RemoteException {
        server.addStock(name, quantity);
    }

    public void removeStock(String name, int quantity) throws RemoteException {
        server.removeStock(name, quantity);
    }
    public void unregisterClient() throws RemoteException {
        server.unregisterClient(this);
    }

    public static void main(String[] args) {
        try {
            StockInterface server = (StockInterface) Naming.lookup("rmi://localhost:8888/StockServer");
            StockClient client = new StockClient(server);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter command (q to quit):");
                System.out.println("c: check stock");
                System.out.println("a: add stock");
                System.out.println("r: remove stock");
                String command = scanner.nextLine();
                if (command.equals("q")) {
                    break;
                } else if (command.equals("c")) {
                    System.out.println("Enter product name:");
                    String name = scanner.nextLine();
                    int quantity = client.getStockQuantity(name);
                    System.out.println(name + ": " + quantity);
                } else if (command.equals("a")) {
                    System.out.println("Enter product name:");
                    String name = scanner.nextLine();
                    System.out.println("Enter quantity to add:");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    client.addStock(name, quantity);
                } else if (command.equals("r")) {
                    System.out.println("Enter product name:");
                    String name = scanner.nextLine();
                    System.out.println("Enter quantity to remove:");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    client.removeStock(name, quantity);
                }
            }
            client.unregisterClient(client);
        } catch (Exception e) {
            System.err.println("StockClient exception:");
            e.printStackTrace();
        }
    }

    @Override
    public void registerClient(StockInterface client) throws RemoteException {
     
    }

    @Override
    public void unregisterClient(StockInterface client) throws RemoteException {
        
    }
}

