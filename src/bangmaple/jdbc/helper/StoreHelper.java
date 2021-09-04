package bangmaple.jdbc.helper;

import bangmaple.jdbc.dao.base.Store;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public interface StoreHelper {

    Store<?, ?> INSTANCE = new Store<>();
    Map<Class<?>, Object> CLASS_STORE = new HashMap<>();

    static <D> D queryClassInstanceFromStore(Class<D> classOf) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!existsClassInStore(classOf)) {
            Constructor<D> constructor = getConstructorFromClass(classOf);
            updateStore(classOf, constructor);
        }
        return getClassFromStore(classOf);
    }

    static <D> boolean existsClassInStore(Class<D> classOf) {
        return CLASS_STORE.containsKey(classOf);
    }

    @SuppressWarnings("unchecked")
    static <D> D getClassFromStore(Class<D> classOf) {
        return (D) CLASS_STORE.get(classOf);
    }

    static <D> void updateStore(Class<D> classOf, Constructor<D> constructor) throws InvocationTargetException,
            InstantiationException, IllegalAccessException {
        D classInstance = constructor.newInstance();
        CLASS_STORE.put(classOf, classInstance);
    }

    @SuppressWarnings("unchecked")
    static <D> Class<D> getClassFromCanonicalClassName(String className) throws ClassNotFoundException {
        return (Class<D>) Class.forName(className);
    }

    static <D> Constructor<D> getConstructorFromClass(Class<D> clazz) throws NoSuchMethodException {
        Constructor<D> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor;
    }
}
