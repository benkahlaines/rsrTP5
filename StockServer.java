import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;

public class StockServer extends UnicastRemoteObject implements StockInterface {
    private static final long serialVersionUID = 1L;
    private List<Stock> stockList = new ArrayList<>();
    private List<StockInterface> clients = new ArrayList<>();

    public StockServer() throws RemoteException {}

    public void registerClient(StockInterface client) throws RemoteException {
        clients.add(client);
    }

    public void unregisterClient(StockInterface client) throws RemoteException {
        clients.remove(client);
    }

    public List<Stock> getStockList() throws RemoteException {
        return stockList;
    }

    public int getStockQuantity(String name) throws RemoteException {
        for (Stock stock : stockList) {
            if (stock.getName().equals(name)) {
                return stock.getQuantity();
            }
        }
        return 0;
    }

    public void addStock(String name, int quantity) throws RemoteException {
        for (Stock stock : stockList) {
            if (stock.getName().equals(name)) {
                stock.setQuantity(stock.getQuantity() + quantity);
                notifyClients();
                return;
            }
        }
        stockList.add(new Stock(name, quantity));
        notifyClients();
    }

    public void removeStock(String name, int quantity) throws RemoteException {
        for (Stock stock : stockList) {
            if (stock.getName().equals(name)) {
                stock.setQuantity(stock.getQuantity() - quantity);
                notifyClients();
                return;
            }
        }
    }

    private void notifyClients() throws RemoteException {
        for (StockInterface client : clients) {
            client.notifyStockChanged();
        }
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(8888);
            StockServer server = new StockServer();
            Naming.rebind("rmi://localhost:8888/StockServer", server);
            System.out.println("StockServer started.");
        } catch (Exception e) {
            System.err.println("StockServer exception:");
            e.printStackTrace();
        }
    }

    @Override
    public void notifyStockChanged() throws RemoteException {
        List<StockInterface> clientsToRemove = new ArrayList<>();
    for (StockInterface client : clients) {
        try {
            client.notifyStockChanged();
        } catch (RemoteException e) {
            // client is no longer reachable, mark for removal
            clientsToRemove.add(client);
        }
    }
    // remove clients that are no longer reachable
    clients.removeAll(clientsToRemove);
    }
}
