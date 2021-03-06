package org.jboss.windup.tests.application.reports.technology;

import java.io.File;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ProjectModel;
import org.jboss.windup.graph.model.resource.FileModel;
import org.jboss.windup.rules.apps.javaee.model.stats.ProjectTechnologiesStatsModel;
import org.jboss.windup.rules.apps.javaee.model.stats.TechnologiesStatsModel;
import org.jboss.windup.rules.apps.javaee.model.stats.TechnologiesStatsService;
import org.jboss.windup.tests.application.WindupArchitectureTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:dklingenberg@gmail.com">David Klingenberg</a>
 */
@RunWith(Arquillian.class)
public class TechnologiesStatsServiceTest extends WindupArchitectureTest
{
    @Deployment
    @AddonDependencies({
                @AddonDependency(name = "org.jboss.windup.graph:windup-graph"),
                @AddonDependency(name = "org.jboss.windup.reporting:windup-reporting"),
                @AddonDependency(name = "org.jboss.windup.exec:windup-exec"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java"),
                @AddonDependency(name = "org.jboss.windup.rules.apps:windup-rules-java-ee"),
                @AddonDependency(name = "org.jboss.windup.utils:windup-utils"),
                @AddonDependency(name = "org.jboss.windup.config:windup-config-groovy"),
                @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
    })
    public static AddonArchive getDeployment()
    {
        TechnologiesStatsService service;
        
        return ShrinkWrap.create(AddonArchive.class)
                    .addBeansXML()
                    .addClass(WindupArchitectureTest.class)
                    .addAsResource(new File("src/test/groovy/GroovyExampleRule.windup.groovy"));
    }

    @Test
    public void testRunWindupTiny() throws Exception
    {
        try (GraphContext context = createGraphContext())
        {
            super.runTest(context, "../test-files/Windup1x-javaee-example-tiny.war", false);
            validateTechReportData(context);
        }
    }

    private void validateTechReportData(GraphContext context)
    {
        Iterable<ProjectTechnologiesStatsModel> allStats = context.service(ProjectTechnologiesStatsModel.class).findAll();
        
        for (ProjectTechnologiesStatsModel projectStats : allStats)
        {
            TechnologiesStatsModel technologiesStatsModel = projectStats.getTechnologiesStatsModel();
            ProjectModel projectModel = projectStats.getProjectModel();
            FileModel fileModel = projectModel.getRootFileModel();
            
            Assert.assertEquals("Windup1x-javaee-example-tiny.war", fileModel.getFileName());
            this.assertFileTypes(technologiesStatsModel);
            this.assertTechnologies(technologiesStatsModel);
        }
    }

    private void assertFileTypes(TechnologiesStatsModel stats)
    {
        Map<String, Integer> fileTypes = stats.getFileTypesMap();

        Assert.assertEquals(1, fileTypes.getOrDefault("class", 1) + 0);
        Assert.assertEquals(1, fileTypes.getOrDefault("xml", 1) + 0);
        Assert.assertEquals(1, fileTypes.getOrDefault("properties", 1) + 0);
        Assert.assertEquals(1, fileTypes.getOrDefault("MF", 1) + 0);
        Assert.assertEquals(1, fileTypes.getOrDefault("war", 1) + 0);
        
        Assert.assertEquals(5, fileTypes.size());
    }

    private void assertTechnologies(TechnologiesStatsModel stats)
    {
        Map<String, Integer> technologies = stats.getTechnologiesMap();
        
        Assert.assertEquals(1, technologies.getOrDefault(TechnologiesStatsModel.STATS_JAVA_CLASSES_ORIGINAL, 1) + 0);
        Assert.assertEquals(1, technologies.getOrDefault(TechnologiesStatsModel.STATS_JAVA_CLASSES_TOTAL, 1) + 0);
        
        Assert.assertEquals(2, technologies.size());
    }
}
