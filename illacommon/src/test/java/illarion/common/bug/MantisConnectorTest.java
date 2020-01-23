package illarion.common.bug;

import biz.futureware.mantis.rpc.soap.client.FilterData;
import biz.futureware.mantis.rpc.soap.client.IssueHeaderData;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MantisConnectorTest {
    private static final Collection<String> EXPECTED_PROJECTS = Arrays.asList("Client", "easyNPC Editor", "Mapeditor");

    @Test
    public void testConstructor() throws ServiceException {
        new MantisConnector();
    }

    @Test
    public void testGetProjects() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();
        Collection<ProjectData> projects = connector.getProjects();

        outer:
        for (String projectName : EXPECTED_PROJECTS) {
            for (ProjectData project : projects) {
                if (project.getName().equals(projectName)) {
                    continue outer;
                }
            }
            fail("Failed to locate required mantis project: " + projectName);
        }
    }

    @Test
    public void testGetProject() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();

        for (String projectName : EXPECTED_PROJECTS) {
            assertNotNull(connector.getProject(projectName), "Fetching project " + projectName + ".");
        }
    }

    @Test
    public void testGetFilters() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();

        List<ProjectData> filteredProjects = getFilteredProjects(connector);

        for (ProjectData project : filteredProjects) {
            Collection<FilterData> filters = connector.getFilters(project);

            assertTrue(
                    filters.stream().anyMatch(f -> f.getName().equals(MantisConnector.STANDARD_FILTER)),
                    "Finding standard filter failed.");
        }
    }

    @Test
    public void testGetFilter() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();

        List<ProjectData> filteredProjects = getFilteredProjects(connector);

        for (ProjectData project : filteredProjects) {
            FilterData filter = connector.getFilter(project);
            assertNotNull(filter, "Locating standard filter failed.");
        }
    }

    @Test
    public void testGetFilteredIssueHeaders() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();

        List<ProjectData> filteredProjects = getFilteredProjects(connector);

        for (ProjectData project : filteredProjects) {
            FilterData filter = connector.getFilter(project);
            if (filter == null) {
                continue;
            }
            Collection<IssueHeaderData> headers = connector.getIssueHeaders(project, filter);
            for (IssueHeaderData header : headers) {
                assertNotNull(header.getSummary());
            }
        }
    }

    @Test
    public void testGetIssueHeaders() throws ServiceException, RemoteException {
        MantisConnector connector = new MantisConnector();

        List<ProjectData> filteredProjects = getFilteredProjects(connector);

        for (ProjectData project : filteredProjects) {
            Collection<IssueHeaderData> headers = connector.getIssueHeaders(project);
            for (IssueHeaderData header : headers) {
                assertNotNull(header.getSummary());
            }
        }
    }

    private static List<ProjectData> getFilteredProjects(@Nonnull MantisConnector connector) throws RemoteException {
        Collection<ProjectData> projects = connector.getProjects();
        List<ProjectData> filteredProjects = new ArrayList<>(EXPECTED_PROJECTS.size());

        for (String projectName : EXPECTED_PROJECTS) {
            filteredProjects.addAll(projects.stream()
                    .filter(project -> project.getName().equals(projectName))
                    .collect(Collectors.toList()));
        }
        return filteredProjects;
    }
}
