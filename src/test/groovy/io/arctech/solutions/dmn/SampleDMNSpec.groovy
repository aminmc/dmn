package io.arctech.solutions.dmn

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder
import org.junit.ClassRule
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Specification

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.decisionService
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.repositoryService
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService

@Narrative('''
A specification that asserts the correct behaviour of mode form decision table
''')
@Deployment(resources = ['determine-dish.dmn'])
class SampleDMNSpec extends Specification {


    @Shared
    @ClassRule
    public ProcessEngineRule processEngineRule = TestCoverageProcessEngineRuleBuilder
            .create()
            .withDetailedCoverageLogging()
            .build()


    def cleanupSpec() {
        repositoryService().createDeploymentQuery().list().each { it ->
            repositoryService().deleteDeployment(it.getId(), true)
        }
    }

    def setup() {
        runtimeService().createProcessInstanceQuery()
                .list().each { it -> runtimeService().deleteProcessInstance(it.id, 'deleted') }
    }

    def cleanup() {
        Mocks.reset()
    }

    def 'can determine dish'() {
        when:
        def decision = decisionService().evaluateDecisionByKey('beverages')
                .variables(['season': 'Spring', 'guestCount': 10, 'guestsWithChildren': false]).evaluate()

        then:
        def beverages = decision.collectEntries("beverages");
        beverages.size() == 2
        beverages.first() == 'Guiness'
        beverages.last() == 'Water'
    }

}
