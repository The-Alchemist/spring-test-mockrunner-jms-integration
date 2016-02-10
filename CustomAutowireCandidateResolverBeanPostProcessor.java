
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;


public class CustomAutowireCandidateResolverBeanPostProcessor implements BeanFactoryPostProcessor {

    private final Iterable<AutowireCandidateResolver> candidateResolvers;

    public CustomAutowireCandidateResolverBeanPostProcessor(Iterable<AutowireCandidateResolver> candidateResolvers) {
        this.candidateResolvers = candidateResolvers;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        DefaultListableBeanFactory  bf = (DefaultListableBeanFactory) beanFactory;
        bf.setAutowireCandidateResolver(new CascadingCandidateResolver(candidateResolvers));
    }

}
