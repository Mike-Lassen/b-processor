package com.bprocessor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.bprocessor.io.Persistence;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class ModelServer
 */
@WebServlet("/models/*")
public class ModelServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SessionFactory factory;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModelServer() {
		super();
		factory = HibernateUtil.getSessionFactory();
		BasicConfigurator.configure();
	}


	@SuppressWarnings("unchecked")
	protected void doListModels(HttpServletRequest request, HttpServletResponse response) {
		Session session = factory.openSession();
		Query query = session.createQuery("from Sketch");
		List<Sketch> sketches = query.list();
		List<SketchInfo> infos = new LinkedList<SketchInfo>();
		for (Sketch sketch : sketches) {
			SketchInfo info = new SketchInfo(sketch.getId(), sketch.getName(), null);
			infos.add(info);
		}
		session.close();

		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getOutputStream(), infos);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
	}

	protected void doGetModel(HttpServletRequest request, HttpServletResponse response, int id) throws ServletException, IOException {
		Session session = factory.openSession();
		Sketch sketch = null;
		
		try {
			sketch = (Sketch) session.get(Sketch.class, id);
		} catch (RuntimeException error) {
			error.printStackTrace();
		}
		
		if (sketch != null) {
			System.out.println("returning model " + sketch.getId());
			try {
				Persistence.serialize(sketch, response.getOutputStream());
			} catch (Exception error) {
				error.printStackTrace();
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error.getMessage());
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Sketch not found in database");
		}
		session.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		response.setContentType("text/json");

		if (path == null || path.equals("/")) {
			doListModels(request, response);
			return;
		}

		path = path.substring(1);
		String[] parts = path.split("/");

		if (parts.length != 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: For input string \"" + path + "\"");
			return;
		}

		int id = 0;

		try { 
			id = Integer.valueOf(parts[0]);
		} catch (NumberFormatException error) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: " + error.getMessage());
			return;
		}

		doGetModel(request, response, id);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Sketch sketch = Persistence.unserialize(request.getInputStream());
		sketch.setId(0);
		Session session = factory.openSession();
		int id = 0;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			id = (Integer) session.save(sketch);
			tx.commit();
		} catch (HibernateException error) {
			System.out.println("error: " + error.getMessage());
			if (tx!=null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.setContentType("text/plain");
		response.setHeader("Location", "models/" + id);
		response.getWriter().print((int) id);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (path == null || path.equals("/")) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot overwrite toplevel");
			return;
		}
		
		path = path.substring(1);
		String[] parts = path.split("/");
		
		if (parts.length != 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: For input string \"" + path + "\"");
			return;
		}

		int id = 0;

		try { 
			id = Integer.valueOf(parts[0]);
		} catch (NumberFormatException error) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: " + error.getMessage());
			return;
		}
		
		Sketch newSketch = Persistence.unserialize(request.getInputStream());
		
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			Sketch sketch = (Sketch) session.get(Sketch.class, id);
			if (sketch != null) {
				Group group = sketch.getGroup();
				sketch.setGroup(null);
				session.delete(group);
				Group newGroup = newSketch.getGroup();
				session.save(newGroup);
				sketch.setGroup(newGroup);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "");
			}
			tx.commit();
		} catch (HibernateException error) {
			if (tx!=null) {
				tx.rollback();
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
		} finally {
			session.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();
		if (path == null || path.equals("/")) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot delete toplevel");
			return;
		}
		
		path = path.substring(1);
		String[] parts = path.split("/");
		
		if (parts.length != 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: For input string \"" + path + "\"");
			return;
		}

		int id = 0;

		try { 
			id = Integer.valueOf(parts[0]);
		} catch (NumberFormatException error) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Number expected: " + error.getMessage());
			return;
		}
		
		
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			Sketch sketch = (Sketch) session.get(Sketch.class, id);
			if (sketch != null) {
				session.delete(sketch);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "");
			}
			tx.commit();
		} catch (HibernateException error) {
			if (tx!=null) {
				tx.rollback();
			}
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "");
		} finally {
			session.close();
		}
	}

}
