package jrails;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JRouter {

    // Inner class to represent each route
    private static class Route {
        String verb;
        String path;
        Class<?> clazz;
        String method;

        Route(String verb, String path, Class<?> clazz, String method) {
            this.verb = verb;
            this.path = path;
            this.clazz = clazz;
            this.method = method;
        }
    }

    // List of routes
    private final List<Route> routes = new ArrayList<>();

    // Adds a route to the router
    public void addRoute(String verb, String path, Class<?> clazz, String method) {
        routes.add(new Route(verb, path, clazz, method));
    }

    // Returns "clazz#method" corresponding to verb+URN; null if no such route
    public String getRoute(String verb, String path) {
        for (Route route : routes) {
            if (route.verb.equals(verb) && route.path.equals(path)) {
                return route.clazz.getSimpleName() + "#" + route.method;
            }
        }
        return null;
    }

    // Call the appropriate controller method and return the result
    public Html route(String verb, String path, Map<String, String> params) {
        for (Route route : routes) {
            // System.out.println("|route|"+verb+path+"$"+route.verb+route.path);
            if (route.verb.equals(verb) && route.path.equals(path)) {
                try {
                    // Create an instance of the controller class
                    Object controllerInstance = route.clazz.getDeclaredConstructor().newInstance();

                    // Get the method by name and parameter type (assuming it takes a Map<String, String>)
                    Method controllerMethod = route.clazz.getMethod(route.method, Map.class);
                    System.out.println("Hit " + route.verb + " " + route.path + " " + route.clazz.getName() + "."+ controllerMethod.getName());

                    // Invoke the method with the params argument
                    return (Html) controllerMethod.invoke(controllerInstance, params);

                } catch (Exception e) {
                    throw new RuntimeException("Error invoking controller method", e);
                }
            }
        }
        throw new UnsupportedOperationException("No route found for " + verb + " " + path);
    }
}
