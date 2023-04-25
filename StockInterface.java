import java.rmi.*;
import java.util.List;

public interface StockInterface extends Remote {
    
    public List<Stock> getStockList() throws RemoteException;
    
    public int getStockQuantity(String name) throws RemoteException;
    
    public void addStock(String name, int quantity) throws RemoteException;
    
    public void removeStock(String name, int quantity) throws RemoteException;
    
    public void registerClient(StockInterface client) throws RemoteException;
    
    public void unregisterClient(StockInterface client) throws RemoteException;
    
    public void notifyStockChanged() throws RemoteException;
}

