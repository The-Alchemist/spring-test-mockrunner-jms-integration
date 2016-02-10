
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.config.TransactionManagementConfigUtils;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.ejb.EJBContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.google.common.base.Verify.*;

@Import({MockRunnerJMSConfiguration.class})
public class ServicesConfiguration {



    /**
     * Hack to generate a mock {@link EJBContext} because we're running outside of an
     * EJB container so we don't have one
     */
    @Bean
    EJBContext ejbContext() {
        ProxyFactory proxyFactory = new ProxyFactory(EJBContext.class, new Interceptor() {
        });
        return (EJBContext) proxyFactory.getProxy();
    }

    @Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
    BeanFactoryTransactionAttributeSourceAdvisor beanFactoryTransactionAttributeSourceAdvisor(TransactionInterceptor transactionInterceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        /*
         * we want to ignore the special Java EE @TransactionalAttribute stuff because some stuff uses REQUIRES_NEW but we don't want that
         * because our tests are all in one transaction
         *
         */
        advisor.setTransactionAttributeSource(new MatchAlwaysTransactionAttributeSource());
        advisor.setAdvice(transactionInterceptor);

        return advisor;
    }



    @Bean
    static CustomAutowireCandidateResolverBeanPostProcessor customAutowireCandidateResolverBPP(DestinationManager dm) {
        return new CustomAutowireCandidateResolverBeanPostProcessor(ImmutableList.of(new JmsResourceCandidateResolver(dm), new LoggerCandidateResolver()));
    }

}
