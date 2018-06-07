package com.hand.bdss.dsmp.component.azkaban;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
public class testSSH {

	public static void main(String[] args)
	{
		String hostname = "hadoop003.edcs.org";
		String username = "is";
		String password = "is";

		try
		{
			/* Create a connection instance */

			Connection conn = new Connection(hostname);

			/* Now connect */

			conn.connect();

			/* Authenticate.
			 * If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */

			boolean isAuthenticated = conn.authenticateWithPassword(username, password);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			/* Create a session */

			Session sess = conn.openSession();
			
			String loginURL="curl -k -X POST --data \"action=login&username=azkaban&password=azkaban\" https://hadoop003.edcs.org:8443";
			//String  createProjectURL = "curl -k -X POST --data \"session.id=15b8323f-5744-49e7-89af-6ad7aa64c24d&name=tancheng&description=11\" https://hadoop003.edcs.org:8443/manager?action=create";
			//String uploadProjectURL="curl -k -i -H "Content-Type: multipart/mixed" -X POST --form 'session.id=28295fde-7446-4d72-8598-fa2aa4f93d0b' --form 'ajax=upload' --form 'file=@tc_test.zip;type=application/zip' --form 'project=tancheng' https://hadoop003.edcs.org:8443/manager";
			sess.execCommand(loginURL);

			System.out.println("Here is some information about the remote host:");

			/* 
			 * This basic example does not handle stderr, which is sometimes dangerous
			 * (please read the FAQ).
			 */

			InputStream stdout = new StreamGobbler(sess.getStdout());

			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

			while (true)
			{
				String line = br.readLine();
				if (line == null)
					break;
				System.out.println(line);
			}

			/* Show exit status, if available (otherwise "null") */

			System.out.println("ExitCode: " + sess.getExitStatus());

			/* Close this session */

			sess.close();

			/* Close the connection */

			conn.close();

		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
			System.exit(2);
		}
	}

}
