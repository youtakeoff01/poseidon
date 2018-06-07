package com.hand.bdss.dsmp.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Test01 {
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		InputStream stdout =null;
		BufferedReader br = null;
		try {
			conn = new Connection("10.7.4.185");
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword("approot", "AntX-XP2017");
			
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			Session sess = conn.openSession();
			String updateCommand = "curl -k -u admin:admin https://10.7.1.55:8089/services/search/jobs -d search='search index=_audit action=search earliest=-1m'";
			System.out.println(updateCommand);
			sess.execCommand(updateCommand);
			
			System.out.println("Here is some information about the remote host:");
			stdout = new StreamGobbler(sess.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				System.out.println(line);
			}
		} finally {
			if(br!=null) {
				br.close();
			}
			if(stdout!=null) {
				stdout.close();
			}
			if(conn!=null) {
				conn.close();
			}
		}
	}
}
