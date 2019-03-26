package io.metadew.iesi.connection.operation;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.metadew.iesi.connection.ArtifactoryConnection;
import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.HostConnection;
import io.metadew.iesi.connection.database.NetezzaDatabaseConnection;
import io.metadew.iesi.connection.database.OracleDatabaseConnection;
import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
import io.metadew.iesi.connection.host.LinuxHostConnection;
import io.metadew.iesi.connection.host.WindowsHostConnection;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionTypeConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.ConnectionParameter;
import io.metadew.iesi.metadata.definition.ConnectionType;
import io.metadew.iesi.metadata.definition.ConnectionTypeParameter;

public class ConnectionOperation {

	private FrameworkExecution frameworkExecution;
	private boolean missingMandatoryFields;
	private List<String> missingMandatoryFieldsList;

	public ConnectionOperation() {
	}

	public ConnectionOperation(FrameworkExecution frameworkExecution) {
		this.setFrameworkExecution(frameworkExecution);
	}

	// Methods
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DatabaseConnection getDatabaseConnection(Connection connection) {
		this.setMissingMandatoryFieldsList(new ArrayList());

		ObjectMapper objectMapper = new ObjectMapper();
		DatabaseConnection databaseConnection = null;
		
		try {
			
			if (connection.getType().equalsIgnoreCase("db.oracle")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String tnsAlias = "";
				String userName = "";
				String userPassword = null;
				String serviceName = "";
	
				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("tnsalias")) {
						tnsAlias = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("service")) {
						serviceName = connectionParameter.getValue();
					}
				}
	
				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equals(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equals(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("tnsalias")) {
							if (tnsAlias.trim().equals(""))
								this.addMissingField("tnsalias");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equals(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equals(""))
								this.addMissingField("password");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("service")) {
							if (userPassword.trim().equals(""))
								this.addMissingField("service");
						}
					}
				}
				
				// Addition for combined mandatory behavour of SERVICE_NM and TNS_ALIAS
				if (tnsAlias.trim().equals("") && serviceName.trim().equals("")) {
					this.addMissingField("service");
					this.addMissingField("tnsalias");
				}
	
				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}
	
				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("tnsalias")) {
							tnsAlias = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tnsAlias);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("service")) {
							serviceName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(serviceName);
						}					
					}
				}
	
				// Convert encrypted integers
				portNumber = Integer.parseInt(portNumberTemp);

				OracleDatabaseConnection oracleDatabaseConnection = new OracleDatabaseConnection(hostName, portNumber, tnsAlias, userName, userPassword, serviceName);
				databaseConnection = objectMapper.convertValue(oracleDatabaseConnection, DatabaseConnection.class);
			} else if (connection.getType().equalsIgnoreCase("db.netezza")) {
				String hostName = "";
				String portNumberTemp = "";
				int portNumber = 0;
				String databaseName = "";
				String userName = "";
				String userPassword = "";
	
				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("host")) {
						hostName = (connectionParameter.getValue());
					} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
						portNumberTemp = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("database")) {
						databaseName = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
						userName = connectionParameter.getValue();
					} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
						userPassword = connectionParameter.getValue();
					}
				}
	
				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							if (hostName.trim().equals(""))
								this.addMissingField("host");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							if (portNumberTemp.trim().equals(""))
								this.addMissingField("port");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							if (databaseName.trim().equals(""))
								this.addMissingField("database");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							if (userName.trim().equals(""))
								this.addMissingField("user");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							if (userPassword.trim().equals(""))
								this.addMissingField("password");
						}
					}
				}
	
				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}
	
				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
							hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
							portNumberTemp = this.getFrameworkExecution().getFrameworkCrypto().decrypt(portNumberTemp);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("database")) {
							databaseName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(databaseName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
							userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
							userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
						}
					}
				}
	
				// Convert encrypted integers
				portNumber = Integer.parseInt(portNumberTemp);
	
				NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(hostName, portNumber, databaseName, userName, userPassword);
				databaseConnection = objectMapper.convertValue(netezzaDatabaseConnection, DatabaseConnection.class);
			} else if (connection.getType().equalsIgnoreCase("db.sqlite")) {
				String filePath = "";
				String fileName = "";
	
				for (ConnectionParameter connectionParameter : connection.getParameters()) {
					if (connectionParameter.getName().equalsIgnoreCase("filepath")) {
						filePath = (connectionParameter.getValue());
					} else if (connectionParameter.getName().equalsIgnoreCase("filename")) {
						fileName = connectionParameter.getValue();
					}
				}
	
				// Check Mandatory Parameters
				this.setMissingMandatoryFields(false);
				ConnectionType connectionType = this.getConnectionType(connection.getType());
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("filepath")) {
							if (filePath.trim().equals(""))
								this.addMissingField("filePath");
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("filename")) {
							if (fileName.trim().equals(""))
								this.addMissingField("fileName");
						}
					}
				}
	
				if (this.isMissingMandatoryFields()) {
					String message = "Mandatory fields missing for connection " + connection.getName();
					throw new RuntimeException(message);
				}
	
	
				// Decrypt Parameters
				for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
					if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
						if (connectionTypeParameter.getName().equalsIgnoreCase("filepath")) {
							filePath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(filePath);
						} else if (connectionTypeParameter.getName().equalsIgnoreCase("filename")) {
							fileName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(fileName);
						}
					}
				}
	
				// Convert encrypted integers
	
				SqliteDatabaseConnection dcSQConnection = new SqliteDatabaseConnection(filePath + File.separator + fileName);
				databaseConnection = objectMapper.convertValue(dcSQConnection, DatabaseConnection.class);
			} else {

				String message = "Database type is not (yet) supported: " + connection.getType();
				throw new RuntimeException(message);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return databaseConnection;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HostConnection getHostConnection(Connection connection){
		this.setMissingMandatoryFieldsList(new ArrayList());

		ObjectMapper objectMapper = new ObjectMapper();
		HostConnection hostConnection = null;
		if (connection.getType().equalsIgnoreCase("host.windows")) {
			String hostName = "";
			String tempPath = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("host")) {
					hostName = (connectionParameter.getValue());
				} else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
					tempPath = connectionParameter.getValue();
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						if (hostName.trim().equals(""))
							this.addMissingField("host");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						if (tempPath.trim().equals(""))
							this.addMissingField("tempPath");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						tempPath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tempPath);
					}
				}
			}

			WindowsHostConnection windowsHostConnection = new WindowsHostConnection(hostName, tempPath);
			hostConnection = objectMapper.convertValue(windowsHostConnection, HostConnection.class);

		} else if (connection.getType().equalsIgnoreCase("host.linux")) {
			String hostName = "";
			int portNumber = 0;
			String userName = "";
			String userPassword = null;
			String tempPath = "";
			String terminalFlag = "";
			String jumpHostConnectionName = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("host")) {
					hostName = (connectionParameter.getValue());
				} else if (connectionParameter.getName().equalsIgnoreCase("port")) {
					portNumber = Integer.parseInt(connectionParameter.getValue());
				} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
					userName = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
					userPassword = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("temppath")) {
					tempPath = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("simulateterminal")) {
					terminalFlag = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("jumphostconnections")) {
					jumpHostConnectionName = connectionParameter.getValue();
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						if (hostName.trim().equals(""))
							this.addMissingField("host");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("port")) {
						if (portNumber == 0)
							this.addMissingField("port");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						if (userName.trim().equals(""))
							this.addMissingField("user");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						if (userPassword.trim().equals(""))
							this.addMissingField("password");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						if (tempPath.trim().equals(""))
							this.addMissingField("tempPath");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
						if (terminalFlag.trim().equals(""))
							this.addMissingField("simulateTerminal");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
						if (jumpHostConnectionName.trim().equals(""))
							this.addMissingField("jumphostConnections");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("host")) {
						hostName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(hostName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("temppath")) {
						tempPath = this.getFrameworkExecution().getFrameworkCrypto().decrypt(tempPath);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("simulateterminal")) {
						terminalFlag = this.getFrameworkExecution().getFrameworkCrypto().decrypt(terminalFlag);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("jumphostconnections")) {
						jumpHostConnectionName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(jumpHostConnectionName);
					}
				}
			}

			LinuxHostConnection linuxHostConnection = new LinuxHostConnection(hostName, portNumber, userName, userPassword, tempPath, terminalFlag, jumpHostConnectionName);
			hostConnection = objectMapper.convertValue(linuxHostConnection, HostConnection.class);
		}

		return hostConnection;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArtifactoryConnection getArtifactoryConnection(Connection connection){
		this.setMissingMandatoryFieldsList(new ArrayList());

		ArtifactoryConnection artifactoryConnection = null;
		if (connection.getType().equalsIgnoreCase("repo.artifactory")) {
			String connectionURL = "";
			String userName = "";
			String userPassword = null;
			String repositoryName = "";

			for (ConnectionParameter connectionParameter : connection.getParameters()) {
				if (connectionParameter.getName().equalsIgnoreCase("url")) {
					connectionURL = (connectionParameter.getValue());
				} else if (connectionParameter.getName().equalsIgnoreCase("user")) {
					userName = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("password")) {
					userPassword = connectionParameter.getValue();
				} else if (connectionParameter.getName().equalsIgnoreCase("repository")) {
					repositoryName = connectionParameter.getValue();
				}
			}

			// Check Mandatory Parameters
			this.setMissingMandatoryFields(false);
			ConnectionType connectionType = this.getConnectionType(connection.getType());
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getMandatory().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
						if (connectionURL.trim().equals(""))
							this.addMissingField("url");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						if (userName.trim().equals(""))
							this.addMissingField("user");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						if (userPassword.trim().equals(""))
							this.addMissingField("password");
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
						if (repositoryName.trim().equals(""))
							this.addMissingField("repository");
					}
				}
			}

			if (this.isMissingMandatoryFields()) {
				String message = "Mandatory fields missing for connection " + connection.getName();
				throw new RuntimeException(message);
			}

			// Decrypt Parameters
			for (ConnectionTypeParameter connectionTypeParameter : connectionType.getParameters()) {
				if (connectionTypeParameter.getEncrypted().equalsIgnoreCase("y")) {
					if (connectionTypeParameter.getName().equalsIgnoreCase("url")) {
						connectionURL = this.getFrameworkExecution().getFrameworkCrypto().decrypt(connectionURL);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("user")) {
						userName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userName);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("password")) {
						userPassword = this.getFrameworkExecution().getFrameworkCrypto().decrypt(userPassword);
					} else if (connectionTypeParameter.getName().equalsIgnoreCase("repository")) {
						repositoryName = this.getFrameworkExecution().getFrameworkCrypto().decrypt(repositoryName);
					}
				}
			}

			artifactoryConnection = new ArtifactoryConnection(connectionURL, userName, userPassword, repositoryName);

		}

		return artifactoryConnection;
	}


	public boolean isOnLocalConnection(HostConnection hostConnection) {
		boolean result = false;
		
		try {
			String localHostName = InetAddress.getLocalHost().getHostName();
			if (hostConnection.getHostName().equalsIgnoreCase(localHostName)) result = true;
		} catch (UnknownHostException e) {
			result = false;
		}

		return result;
	}

	public ConnectionType getConnectionType(String connectionTypeName) {
		ConnectionTypeConfiguration connectionTypeConfiguration = new ConnectionTypeConfiguration(this.getFrameworkExecution());
		ConnectionType connectionType = null;
		
		try {
			connectionType = connectionTypeConfiguration.getConnectionType(connectionTypeName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		
		return connectionType;
	}

	protected void addMissingField(String fieldName) {
		this.setMissingMandatoryFields(true);
		this.getMissingMandatoryFieldsList().add(fieldName);
	}

	// Getters and Setters
	public boolean isMissingMandatoryFields() {
		return missingMandatoryFields;
	}

	public void setMissingMandatoryFields(boolean missingMandatoryFields) {
		this.missingMandatoryFields = missingMandatoryFields;
	}

	public List<String> getMissingMandatoryFieldsList() {
		return missingMandatoryFieldsList;
	}

	public void setMissingMandatoryFieldsList(List<String> missingMandatoryFieldsList) {
		this.missingMandatoryFieldsList = missingMandatoryFieldsList;
	}

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}

}
