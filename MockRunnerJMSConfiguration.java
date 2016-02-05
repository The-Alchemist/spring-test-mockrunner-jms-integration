
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mockrunner.jms.ConfigurationManager;
import com.mockrunner.jms.DestinationManager;
import com.mockrunner.mock.jms.JMSMockObjectFactory;
import com.mockrunner.mock.jms.MockQueueConnectionFactory;

/**
 * Support for using {@link Inject}ing <a href="http://mockrunner.github.io/mockrunner/examplesjms.html">MockRunner</a> mocks into tests
 * @author kpietrzak
 * @see http://mockrunner.github.io/mockrunner/examplesjms.html
 */
@Configuration
public class MockRunnerJMSConfiguration {
    @Bean
    JMSMockObjectFactory jmsMockObjectFactory() {
        return new JMSMockObjectFactory();
    }

    @Bean
    DestinationManager destinationManager(JMSMockObjectFactory jmsMockFactory) {
        return jmsMockFactory.getDestinationManager();
    }

    @Bean
    ConfigurationManager configurationManager(JMSMockObjectFactory jmsMockFactory) {
        return jmsMockFactory.getConfigurationManager();
    }

    @Bean
    MockQueueConnectionFactory mockJmsQueueConnectionFactory(JMSMockObjectFactory jmsMockFactory) {
        return jmsMockFactory.getMockQueueConnectionFactory();
    }

    /**
     * MockRunner doesn't support JMS2 {@link JMSContext}'s :(
     *
     * So someone wrote a "JMS1" -> "JMS2" wrapper
     *
     *
     * @param connectionFactory
     * @return
     * @see https://github.com/melowe/jms2-compat/tree/master/src/main/java/com/melowe/jms2/compat
     */
    @Bean
    JMSContext mockJmsContext(MockQueueConnectionFactory connectionFactory) {
        ConnectionFactory connectonFactory = new com.melowe.jms2.compat.Jms2ConnectionFactory(connectionFactory);

        JMSContext context = connectonFactory.createContext();
        return context;
    }
}
