package org.jboss.windup.rules.apps.java.scan.provider;

import com.thinkaurelius.titan.diskstorage.util.DirectoryUtil;
import java.io.File;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.jboss.windup.config.query.WindupConfigurationQuery;
import org.jboss.windup.config.query.OutAndBackCriterion;
import org.jboss.windup.config.AbstractRuleProvider;
import org.jboss.windup.config.GraphRewrite;
import org.jboss.windup.config.KeepWorkDirsOption;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.GraphOperation;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.config.operation.IterationProgress;
import org.jboss.windup.config.phase.PostFinalizePhase;
import org.jboss.windup.config.phase.PostReportRenderingPhase;
import org.jboss.windup.config.query.Query;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.graph.model.ArchiveModel;
import org.jboss.windup.rules.apps.java.scan.operation.DeleteWorkDirsOperation;
import org.jboss.windup.rules.apps.java.scan.operation.UnzipArchiveToOutputFolder;
import static org.jboss.windup.rules.apps.java.scan.provider.DeleteWorkDirsAtTheEndRuleProvider.KEEP_WORK_FILES;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Not;
import org.ocpsoft.rewrite.context.EvaluationContext;

/**
 * Deletes the directories with the content unzipped from the archives.
 * The graph can't be deleted at this point so that's left up to the Bootstrap.
 *
 * @author Ondrej Zizka
 * @see WINDUP-686
 */
@RuleMetadata(
    after = {PostReportRenderingPhase.class},
    // I don't want to create a dependency: before = {ExecutionTimeReportRuleProvider.class},
    description = "Deletes the temporary data Windup analyzes: the unzipped archives, and the graph data."
            + " Use --" + KEEP_WORK_FILES + " to keep them.",
    phase = PostFinalizePhase.class
)
public class DeleteWorkDirsAtTheEndRuleProvider extends AbstractRuleProvider
{
    public static final String KEEP_WORK_FILES = "keepTempFiles";

    @Override
    public Configuration getConfiguration(GraphContext graphContext)
    {
        // @formatter:off
        return ConfigurationBuilder.begin()
        .addRule()
        .when(
            Not.any(WindupConfigurationQuery.hasOption(KeepWorkDirsOption.class).as("nothing")),
            Query.fromType(ArchiveModel.class).piped(new OutAndBackCriterion(ArchiveModel.UNZIPPED_DIRECTORY)).as("archives")
        )
        .perform(
            Iteration.over("archives").perform(
                DeleteWorkDirsOperation.delete(),
                IterationProgress.monitoring("Deleted archive unzip directory", 1)
            ).endIteration()
        )
        .addRule().perform(
            new GraphOperation()
            {
                public void perform(GraphRewrite event, EvaluationContext context)
                {
                    File archivesDir = UnzipArchiveToOutputFolder.getArchivesDirLocation(event.getGraphContext()).toFile();
                    if (archivesDir.exists() && archivesDir.isDirectory())
                        if(archivesDir.list().length == 0)
                            archivesDir.delete();
                }

                public String toString()
                {
                    return "Delete archives/ if empty";
                }
            }
        )
        ;
        // @formatter:off
    }

}
