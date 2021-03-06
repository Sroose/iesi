package io.metadew.iesi.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.metadata.backup.BackupExecution;
import io.metadew.iesi.metadata.configuration.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.metadata.operation.MetadataRepositoryOperation;
import io.metadew.iesi.metadata.restore.RestoreExecution;

/**
 * The metadata launcher is entry point to launch all configuration management operations.
 *
 * @author peter.billen
 */
public class MetadataLauncher
{

	private static boolean actionMatch = false;

	@SuppressWarnings({"unchecked", "rawtypes", "unused"})
	public static void main(String[] args)
	{

		Option oHelp = new Option("help", "print this message");
		Option oType = new Option("type", true, "define the type of metadata repository");
		Option oConfig = new Option("config", true, "define the metadata repository config");
		Option oBackup = new Option("backup", "create a backup of the entire metadata repository");
		Option oRestore = new Option("restore", "restore a backup of the metadata repository");
		Option oPath = new Option("path", true, "path to be used to for backup or restore");
		Option oDrop = new Option("drop", "drop all metadata tables in the metadata repository");
		Option oCreate = new Option("create", "create all metadata tables in the metadata repository");
		Option oClean = new Option("clean", "clean all tables in the metadata repository");
		Option oLoad = new Option("load", "load metadata file from the input folder into the metadata repository");
		Option oDdl = new Option("ddl",
					"generate ddl output instead of execution in the metadata repository, to be combined with options: create, drop");

		String filesHelp = "";
		filesHelp += "Following options are possible:";
		filesHelp += "\n";
		filesHelp += "-(1) a single file name including extension";
		filesHelp += "\n";
		filesHelp += "--Example: Script.json";
		filesHelp += "\n";
		filesHelp += "-(2) list of files separated by commas";
		filesHelp += "\n";
		filesHelp += "--Example: Script1.json,Script2.json";
		filesHelp += "\n";
		filesHelp += "-(3) a regular expression written as function =regex([your expression])";
		filesHelp += "\n";
		filesHelp += "--Example: =regex(.+\\json) > this will load all files";
		filesHelp += "\n";
		Option oFiles = new Option("files", true,
					"filename(s) to load from the input folder into the metadata repository" + "\n" + filesHelp);

		// create Options object
		Options options = new Options();
		// add options
		options.addOption(oHelp);
		options.addOption(oType);
		options.addOption(oConfig);
		options.addOption(oBackup);
		options.addOption(oRestore);
		options.addOption(oPath);
		options.addOption(oDrop);
		options.addOption(oCreate);
		options.addOption(oClean);
		options.addOption(oLoad);
		options.addOption(oDdl);
		options.addOption(oFiles);

		// create the parser
		CommandLineParser parser = new DefaultParser();
		try
		{
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help"))
			{
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("[command]", options);
				System.exit(0);
			}

			Context context = new Context();
			context.setName("metadata");
			context.setScope("");
			FrameworkExecution frameworkExecution = new FrameworkExecution(new FrameworkExecutionContext(context), "owner");
			MetadataRepositoryOperation metadataRepositoryOperation = null;
			List<MetadataRepositoryConfiguration> metadataRepositoryConfigurationList = new ArrayList();

			String type = "";
			if (line.hasOption("type"))
			{
				type = line.getOptionValue("type");
				System.out.println("Option -type (type) value = " + type);
			}
			else
			{
				System.out.println("Option -type (type) missing");
				System.exit(1);
			}

			if (line.hasOption("config"))
			{
				String config = line.getOptionValue("config");

				ConfigFile configFile = frameworkExecution.getFrameworkControl().getConfigFile("keyvalue",
							frameworkExecution.getFrameworkConfiguration().getFolderConfiguration().getFolderAbsolutePath("conf")
										+ File.separator + config);

				MetadataRepositoryConfiguration metadataRepositoryConfiguration = new MetadataRepositoryConfiguration(
							frameworkExecution.getFrameworkConfiguration(), frameworkExecution.getFrameworkControl(), configFile, "owner");

				metadataRepositoryConfigurationList.add(metadataRepositoryConfiguration);

			}
			else
			{
				switch (type)
				{
					case "connectivity" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getConnectivityRepositoryConfiguration());
						break;
					case "control" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getControlRepositoryConfiguration());
						break;
					case "design" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getDesignRepositoryConfiguration());
						break;
					case "result" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getResultRepositoryConfiguration());
						break;
					case "trace" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getTraceRepositoryConfiguration());
						break;
					case "general" :
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getConnectivityRepositoryConfiguration());
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getControlRepositoryConfiguration());
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getDesignRepositoryConfiguration());
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getResultRepositoryConfiguration());
						metadataRepositoryConfigurationList
									.add(frameworkExecution.getMetadataControl().getTraceRepositoryConfiguration());
						break;
					default :
						System.out.println("Unkknow Option -type (type) = " + type);
						System.exit(1);
				}
			}
			// Backup
			if (line.hasOption("backup"))
			{
				for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList)
				{
					if (actionMatch)
					{
						System.out.println();
					}
					writeHeaderMessage();
					System.out.println("Option -backup (backup) selected");
					actionMatch = true;

					// Get path value
					String path = "";
					if (line.hasOption("path"))
					{
						path = line.getOptionValue("path");
						System.out.println("Option -path (path) value = " + path);
					}
					else
					{
						System.out.println("Option -path (path) not provided, using default location");
					}

					// Execute
					BackupExecution backupExecution = new BackupExecution();
					backupExecution.execute(path);
					writeFooterMessage();
					System.exit(0);
				}
			}

			// Restore
			if (line.hasOption("restore"))
			{
				for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList)
				{
					if (actionMatch)
					{
						System.out.println();
					}
					writeHeaderMessage();
					System.out.println("Option -restore (restore) selected");
					System.out.println();
					actionMatch = true;

					// Get path value
					String path = "";
					if (line.hasOption("path"))
					{
						path = line.getOptionValue("path");
						System.out.println("Option -path (path) value = " + path);
					}
					else
					{
						System.out.println("Option -path (path) missing");
						System.exit(1);
					}

					// Execute
					RestoreExecution restoreExecution = new RestoreExecution();
					restoreExecution.execute(path);
					writeFooterMessage();
					System.exit(0);
				}
			}

			// Drop
			if (line.hasOption("drop"))
			{
				for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList)
				{
					metadataRepositoryOperation = new MetadataRepositoryOperation(frameworkExecution, metadataRepositoryConfiguration);

					if (actionMatch)
					{
						System.out.println();
					}
					writeHeaderMessage();
					System.out.println("Option -drop (drop) selected");
					System.out.println();
					actionMatch = true;
					metadataRepositoryOperation.drop();
					writeFooterMessage();
				}
			}

			// Create
			for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList)
			{
				metadataRepositoryOperation = new MetadataRepositoryOperation(frameworkExecution, metadataRepositoryConfiguration);
				if (line.hasOption("create"))
				{
					if (actionMatch)
					{
						System.out.println();
					}
					writeHeaderMessage();
					System.out.println("Option -create (create) selected");
					actionMatch = true;
					boolean ddl;
					if (line.hasOption("ddl"))
					{
						System.out.println("Option -ddl (ddl) selected");
						ddl = true;
					}
					else
					{
						ddl = false;
					}
					System.out.println();
					metadataRepositoryOperation.create(ddl);
					writeFooterMessage();
				}
			}

			// clean
			if (line.hasOption("clean"))
			{
				for (MetadataRepositoryConfiguration metadataRepositoryConfiguration : metadataRepositoryConfigurationList)
				{
					metadataRepositoryOperation = new MetadataRepositoryOperation(frameworkExecution, metadataRepositoryConfiguration);
					if (actionMatch)
					{
						System.out.println();
					}
					writeHeaderMessage();
					System.out.println("Option -clean (clean) selected");
					System.out.println();
					actionMatch = true;
					metadataRepositoryOperation.cleanAllTables();
					writeFooterMessage();
				}

			}

			// load
			if (line.hasOption("load"))
			{
				if (actionMatch)
				{
					System.out.println();
				}
				writeHeaderMessage();
				System.out.println("Option -load (load) selected");
				System.out.println();
				actionMatch = true;
				if (line.hasOption("files"))
				{
					String files = "";
					files = line.getOptionValue("files");
					metadataRepositoryOperation.loadMetadataRepository(metadataRepositoryConfigurationList, files);
				}
				else
				{
					metadataRepositoryOperation.loadMetadataRepository(metadataRepositoryConfigurationList);
				}
				writeFooterMessage();
			}

			if (actionMatch)
			{
				System.out.println();
				System.out.println("metadata.launcher.end");
				System.exit(0);
			}
			else
			{
				System.out.println("No valid arguments have been provided, type -help for help.");
			}

		}
		catch (

		ParseException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	private static void writeHeaderMessage()
	{
		if (!actionMatch)
		{
			System.out.println("metadata.launcher.start");
			System.out.println();
		}
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	private static void writeFooterMessage()
	{
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

}