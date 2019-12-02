package com.tec.domingostore.DB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.DaoSession;
import com.tec.domingostore.DB.Entities.MonthlyPayment;
import com.tec.domingostore.DB.Entities.MonthlyPaymentDao;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.PaymentDao;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.SaleDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.Main.Products.ProductsRecyclerViewAdapter;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SyncFirebase {

    public static FirebaseFirestore db;

    static public final String UPDATED_SELLERS          = "UPDATED_SELLERS";
    static public final String UPDATED_PRODUCTS         = "UPDATED_PRODUCTS";
    static public final String UPDATED_CUSTOMERS        = "UPDATED_CUSTOMERS";
    static public final String UPDATED_MONTHLY_PAYMENT  = "UPDATED_MONTHLY_PAYMENT";
    static public final String UPDATED_PAYMENTS         = "UPDATED_PAYMENTS";
    static public final String UPDATED_SALES            = "UPDATED_SALES";

    public static void setup(Context context){
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();

        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        StorageFirebase.setup();

        syncColletions(context);
    }

    public static void syncColletions(Context context){

        if (context != null){
            DAOAccess.setup(context);
                syncProducts(context);
                syncSeller(context);
                syncCustomer(context);
                syncPayment(context);
                downloadMonthlyPayment(context);
                syncSales(context);
        }

    }

    public static void syncProducts(final Context context){

        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Sync.eq(false));
        List<Product> productList   = productQueryBuilder.list();

        if (productList.size() > 0) {
            for (Product currentProduct : productList) {

                if (currentProduct.equals(productList.get(productList.size() - 1)))
                    Product.uploadToServer(db, currentProduct, context);
                else
                    Product.uploadToServer(db, currentProduct, null);
            }
        }else {
            downloadProducts(context);
        }
    }

    public static void syncSeller(final Context context){

        QueryBuilder<Seller> sellerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        sellerQueryBuilder.where(SellerDao.Properties.Sync.eq(false));
        List<Seller> sellerList   = sellerQueryBuilder.list();

        if (sellerList.size() > 0) {
            for (Seller currentSeller : sellerList) {
                if (currentSeller.equals(sellerList.get(sellerList.size() - 1)))
                    Seller.uploadToServer(db, currentSeller, context);
                else
                    Seller.uploadToServer(db, currentSeller, null);
            }
        }else {
            downloadSeller(context);
        }

    }

    public static void syncCustomer(Context context){
        QueryBuilder<Customer> customerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.where(CustomerDao.Properties.Sync.eq(false));
        List<Customer> customerList   = customerQueryBuilder.list();

        if (customerList.size() > 0) {
            for (Customer currentCustomer : customerList) {
                if (currentCustomer.equals(customerList.get(customerList.size() - 1)))
                    Customer.uploadToServer(db, currentCustomer, context);
                else
                    Customer.uploadToServer(db, currentCustomer, null);
            }
        }else {
            downloadCustomers(context);
        }
    }

    public static void syncPayment(Context context){
        QueryBuilder<Payment> paymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getPaymentDao().queryBuilder();
        paymentQueryBuilder.where(PaymentDao.Properties.Sync.eq(false));
        List<Payment> paymentList   = paymentQueryBuilder.list();

        if (paymentList.size() > 0) {
            for (Payment currentPayment : paymentList) {
                if (currentPayment.equals(paymentList.get(paymentList.size() - 1)))
                    Payment.uploadToServer(db, currentPayment, context);
                else
                    Payment.uploadToServer(db, currentPayment, null);
            }
        }else {
            downloadPayment(context);
        }
    }

    public static void syncSales(Context context){
        QueryBuilder<Sale> saleQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        saleQueryBuilder.where(SaleDao.Properties.Sync.eq(false));
        List<Sale> saleList   = saleQueryBuilder.list();

        if (saleList.size() > 0) {
            for (Sale currentSale : saleList) {
                if (currentSale.equals(saleList.get(saleList.size() - 1)))
                    Sale.uploadToServer(db, currentSale, context, null);
                else
                    Sale.uploadToServer(db, currentSale, null, null);
            }
        }else {
            downloadSales(context);
        }
    }



    public static void downloadProducts(final Context context){
        final Intent intent = new Intent(UPDATED_PRODUCTS);

        db.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                Product currentProduct  = Product.processFromServer(document.getId(), document.getData());

                                QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
                                productQueryBuilder.where(ProductDao.Properties.Db_id.eq(document.getId()));
                                List<Product> productList   = productQueryBuilder.list();
                                currentProduct.setSync(true);

                                if (productList.size() > 0) {
                                    currentProduct.setId(productList.get(0).getId());

                                    if (currentProduct.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentProduct);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentProduct);

                                }else if (currentProduct.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentProduct);
                                }
                            }
                            intent.putExtra("message","Productos actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar los productos");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
    }

    public static void downloadSeller(final Context context){
        final Intent intent = new Intent(UPDATED_SELLERS);

        db.collection("seller")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                Seller currentSeller  = Seller.processFromServer(document.getId(), document.getData());

                                QueryBuilder<Seller> sellerQueryBuilder1   = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
                                sellerQueryBuilder1.where(SellerDao.Properties.Db_id.eq(document.getId()));
                                List<Seller> sellerList   = sellerQueryBuilder1.list();
                                currentSeller.setSync(true);

                                if (sellerList.size() > 0) {
                                    currentSeller.setId(sellerList.get(0).getId());

                                    if (currentSeller.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentSeller);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentSeller);

                                }else if (currentSeller.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentSeller);
                                }
                            }
                            intent.putExtra("message","Vendedores actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar a los vendedores");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
    }

    public static void downloadCustomers(final Context context){

        final Intent intent = new Intent(UPDATED_CUSTOMERS);

        db.collection("customer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                Customer currentCustomer  = Customer.processFromServer(document.getId(), document.getData());

                                QueryBuilder<Customer> customerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
                                customerQueryBuilder.where(CustomerDao.Properties.Db_id.eq(document.getId()));
                                List<Customer> customerList   = customerQueryBuilder.list();
                                currentCustomer.setSync(true);

                                if (customerList.size() > 0) {
                                    currentCustomer.setId(customerList.get(0).getId());

                                    if (currentCustomer.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentCustomer);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentCustomer);

                                }else if (currentCustomer.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentCustomer);
                                }
                            }
                            intent.putExtra("message","Clientes actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar a los clientes");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
    }

    public static void downloadMonthlyPayment(final Context context){
        final Intent intent = new Intent(UPDATED_MONTHLY_PAYMENT);

        db.collection("monthly_payment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                MonthlyPayment currentMonthlyPayment  = MonthlyPayment.processFromServer(document.getId(), document.getData());

                                QueryBuilder<MonthlyPayment> monthlyPaymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getMonthlyPaymentDao().queryBuilder();
                                monthlyPaymentQueryBuilder.where(MonthlyPaymentDao.Properties.Db_id.eq(document.getId()));
                                List<MonthlyPayment> monthlyPaymentList   = monthlyPaymentQueryBuilder.list();
                                currentMonthlyPayment.setSync(true);

                                if (monthlyPaymentList.size() > 0) {
                                    currentMonthlyPayment.setId(monthlyPaymentList.get(0).getId());

                                    if (currentMonthlyPayment.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentMonthlyPayment);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentMonthlyPayment);

                                }else if (currentMonthlyPayment.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentMonthlyPayment);
                                }
                            }
                            intent.putExtra("message","Plazos actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar los plazos");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });

    }

    public static void downloadPayment(final Context context){
        final Intent intent   = new Intent(UPDATED_PAYMENTS);

        db.collection("payment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                Payment currentPayment  = Payment.processFromServer(document.getId(), document.getData(),document.getTimestamp("payment_date").toDate());

                                QueryBuilder<Payment> paymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getPaymentDao().queryBuilder();
                                paymentQueryBuilder.where(PaymentDao.Properties.Db_id.eq(document.getId()));
                                List<Payment> paymentList   = paymentQueryBuilder.list();
                                currentPayment.setSync(true);

                                if (paymentList.size() > 0) {
                                    currentPayment.setId(paymentList.get(0).getId());

                                    if (currentPayment.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentPayment);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentPayment);

                                }else if (currentPayment.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentPayment);
                                }
                            }
                            intent.putExtra("message","Pagos actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar los Pagos");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
    }

    public static void downloadSales(final Context context){
        final Intent intent   = new Intent(UPDATED_SALES);

        db.collection("sale")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot document: task.getResult()) {
                                //Log.d("Seller-Sync", document.getId() + " => " + document.getData());
                                Sale currentSale  = Sale.processFromServer(document.getId(), document.getData());

                                QueryBuilder<Sale> saleQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
                                saleQueryBuilder.where(SaleDao.Properties.Db_id.eq(document.getId()));
                                List<Sale> saleList   = saleQueryBuilder.list();
                                currentSale.setSync(true);

                                if (saleList.size() > 0) {
                                    currentSale.setId(saleList.get(0).getId());

                                    if (currentSale.getStatus())
                                        DAOAccess.sharedInstance().getDaoSession().update(currentSale);
                                    else
                                        DAOAccess.sharedInstance().getDaoSession().delete(currentSale);

                                }else if (currentSale.getStatus()){
                                    DAOAccess.sharedInstance().getDaoSession().insert(currentSale);
                                }
                            }
                            intent.putExtra("message","Ventas actualizados");

                        }else {
                            intent.putExtra("message","No se pudo actualizar los Pagos");
                        }

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
    }

}
