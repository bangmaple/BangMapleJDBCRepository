package bangmaple.jdbc.dao.base;

import bangmaple.jdbc.helper.StoreHelper;
import bangmaple.jdbc.repository.JdbcRepository;

import java.lang.reflect.InvocationTargetException;

public class Store<T, ID> extends JdbcRepository<T, ID> implements StoreHelper {

    public static <D> D select(Class<D> classOf) throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        synchronized (INSTANCE) {
            return StoreHelper.queryClassInstanceFromStore(classOf);
        }
    }

    public static <D> D select(String classPath) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        synchronized (INSTANCE) {
            Class<D> clazz = StoreHelper.getClassFromCanonicalClassName(classPath);
            return StoreHelper.queryClassInstanceFromStore(clazz);
        }
    }
}
