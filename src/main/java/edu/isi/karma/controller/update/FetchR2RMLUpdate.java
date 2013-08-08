/*******************************************************************************
 * Copyright 2013 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/

package edu.isi.karma.controller.update;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.isi.karma.view.VWorkspace;

public class FetchR2RMLUpdate extends AbstractUpdate {

	private static Logger logger = LoggerFactory.getLogger(FetchR2RMLUpdate.class);
	
	private HashMap<String, String> models;
	
	private enum JsonKeys {
		models
	}
	
	private enum JsonValues {
		R2RMLModelUpdate
	}

	public FetchR2RMLUpdate(HashMap<String, String> model_list)
	{
		this.models = model_list;
	}
	@Override
	public void generateJson(String prefix, PrintWriter pw, VWorkspace vWorkspace) 
	{
		JSONArray list = new JSONArray();
		try {
			Iterator<String> itr = models.keySet().iterator();
			while(itr.hasNext()) {
				JSONObject obj = new JSONObject();
				String key = itr.next();
				obj.put("name", key);
				obj.put("url", models.get(key));
				list.put(obj);
				
			}
			JSONObject obj = new JSONObject();
			obj.put(GenericJsonKeys.updateType.name(), JsonValues.R2RMLModelUpdate.name());
			obj.put(JsonKeys.models.name(), list);
			pw.print(obj.toString());
		} catch (Exception e) {
			logger.error("Error generating JSON!", e);
		}
	}
}