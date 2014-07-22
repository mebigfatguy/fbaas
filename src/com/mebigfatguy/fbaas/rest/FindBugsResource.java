/** fbaas - FindBugs as a Service. 
 * Copyright 2014 MeBigFatGuy.com 
 * Copyright 2014 Dave Brosius 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 */
package com.mebigfatguy.fbaas.rest;

import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mebigfatguy.fbaas.FBJob;
import com.mebigfatguy.fbaas.Titles;


@Path("/findbugs")
public class FindBugsResource {
	
	@Context 
	ServletContext context;
	
	@GET
	@Path("/text")
	@Produces(MediaType.APPLICATION_JSON)
	public Titles getText(@Context HttpServletRequest request) {
		return new Titles(request.getLocale());
	}
	
	@GET
	@Path("/run/{groupId}/{artifactId}/{version}/{email}")
	public Response findBugs(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("version") String version, @PathParam("email") String email) {
		FBJob job = new FBJob(groupId, artifactId, version, email);
		
		@SuppressWarnings("unchecked")
		ArrayBlockingQueue<FBJob> queue = (ArrayBlockingQueue<FBJob>) context.getAttribute("queue");
		queue.add(job);
		
		return Response.ok().build();
	}
}
