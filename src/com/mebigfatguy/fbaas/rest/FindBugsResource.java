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

import java.util.Queue;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mebigfatguy.fbaas.Artifact;
import com.mebigfatguy.fbaas.FindBugsResultsProcessor;
import com.mebigfatguy.fbaas.Results;
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
	@Path("/run/{groupId}/{artifactId}/{version}")
	public Response findBugs(@Context HttpServletRequest request, @PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("version") String version) {
		Artifact job = new Artifact(groupId, artifactId, version);
		
		FindBugsResultsProcessor processor = (FindBugsResultsProcessor) context.getAttribute("results");
		Results r = processor.getResults(request.getLocale(), job);
		
		if (r.getBugs() == null) {
    		@SuppressWarnings("unchecked")
    		Queue<Artifact> queue = (Queue<Artifact>) context.getAttribute("queue");
    		queue.add(job);
		}
		
		return Response.ok(r).build();
	}
}
