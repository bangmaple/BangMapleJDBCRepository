package bangmaple.helper;


import bangmaple.helper.repository.JdbcRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Store<T, ID> extends JdbcRepository<T, ID> implements Cloneable {

    private static final Store<?, ?> INSTANCE = new Store<>();

    private final Map<Class<?>, Object> mapHolder = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static <D> D select(Class<D> classOf) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        synchronized (INSTANCE) {
            queryStore(classOf);
            return (D) INSTANCE.mapHolder.get(classOf);
        }
    }

    @SuppressWarnings("unchecked")
    public static <D> D select(String classOfStr) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        synchronized (INSTANCE) {
            Class<D> classOf = (Class<D>) Class.forName(classOfStr);
            queryStore(classOf);
            return (D) INSTANCE.mapHolder.get(classOf);
        }
    }

    @SuppressWarnings("unchecked")
    private static <D> void queryStore(Class<D> classOf) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!INSTANCE.mapHolder.containsKey(classOf)) {
            Class<D> clazz = (Class<D>) Class.forName(classOf.getCanonicalName());
            Constructor<D> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            D classInstance = constructor.newInstance();
            INSTANCE.mapHolder.put(classOf, classInstance);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
