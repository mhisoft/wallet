package org.mhisoft.wallet.service;

import java.util.HashMap;
import java.util.Map;

import org.mhisoft.wallet.model.WalletSettings;

/**
 * Description:  Manual service registry.
 *
 * @author Tony Xue
 * @since Apr, 2016
 */
public class ServiceRegistry {

	public static ServiceRegistry instance = new ServiceRegistry();

	private  ServiceRegistry() {
		//constructor.
		instance =  this;
		init();
	}


	//keeps the instances of the singletons
	protected static Map<Class, Object> applicationContext = new HashMap<>();


	public void init() {
		//initialize the context.
	}

	@SuppressWarnings("unchecked")
	public <T>  T getService( final BeanType beanType, final Class<T> serviceClass) {
		if (beanType==BeanType.prototype)
			return createNewInstance(serviceClass);
		else
		{
			if (applicationContext.get(serviceClass)==null) {
				applicationContext.put(serviceClass, createNewInstance(serviceClass));
			}
			return (T)applicationContext.get(serviceClass);

		}
	}

	/**
	 * Set a known singleton instance to the application context.
	 * @param instance
	 * @param <T>
	 */
	public <T> void registerSingletonService(T instance) {
		applicationContext.put(instance.getClass(), instance) ;
	}


	private <T>  T createNewInstance(final Class<T> clazz )  {
//		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		if (loader==null)
//			loader = callerClass.getClassLoader();

		try {
			T newInstane = clazz.newInstance();
			return newInstane;
		} catch (Exception e) {
			throw new RuntimeException("can't create instance for "+clazz, e )  ;
		}

	}

	public WalletSettings getWalletSettings(){
		 return getService(BeanType.singleton, WalletSettings.class)  ;
	}
}
