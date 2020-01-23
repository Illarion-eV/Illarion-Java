package illarion.common.bug;

import biz.futureware.mantis.rpc.soap.client.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.rpc.ServiceException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;

/**
 * This is a utility class for communicating with the mantis bug tracker.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MantisConnector {
    @Nonnull
    private static final String USERNAME = "Java Reporting System";
    @Nonnull
    private static final String PASSWORD = "dA23MvKT1KDm4k0bQmMS";

    @Nonnull
    static final String STANDARD_FILTER = "Automatic Error Reports";

    @Nonnull
    private static final String SOAP_CONNECTOR = "https://illarion.org/mantis/api/soap/mantisconnect.php";

    @Nonnull
    private final MantisConnectPortType connectPortType;

    MantisConnector() throws ServiceException {
        MantisConnectLocator locator = new MantisConnectLocator();
        locator.setMantisConnectPortEndpointAddress(SOAP_CONNECTOR);
        connectPortType = locator.getMantisConnectPort();
    }

    @Nonnull
    Collection<ProjectData> getProjects() throws RemoteException {
        return Arrays.asList(connectPortType.mc_projects_get_user_accessible(USERNAME, PASSWORD));
    }

    @Nullable
    ProjectData getProject(@Nonnull String projectName) throws RemoteException {
        Collection<ProjectData> projects = getProjects();

        for (ProjectData project : projects) {
            if (project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    @Nonnull
    Collection<FilterData> getFilters(@Nonnull ProjectData project) throws RemoteException {
        return Arrays.asList(connectPortType.mc_filter_get(USERNAME, PASSWORD, project.getId()));
    }

    @Nullable
    FilterData getFilter(@Nonnull ProjectData project) throws RemoteException {
        return getFilter(project, STANDARD_FILTER);
    }

    @Nullable
    FilterData getFilter(@Nonnull ProjectData project, @Nonnull String filterName) throws RemoteException {
        Collection<FilterData> filters = getFilters(project);

        for (FilterData filter : filters) {
            if (filter.getName().equals(filterName)) {
                return filter;
            }
        }
        return null;
    }

    @Nonnull
    Collection<IssueHeaderData> getIssueHeaders(@Nonnull ProjectData project) throws RemoteException {
        return Arrays.asList(
                connectPortType.mc_project_get_issue_headers(USERNAME, PASSWORD, project.getId(),
                        BigInteger.ZERO, BigInteger.valueOf(-1)));
    }

    @Nonnull
    Collection<IssueHeaderData> getIssueHeaders(@Nonnull ProjectData project,
                                                @Nullable FilterData filter) throws RemoteException {
        if (filter == null) {
            return getIssueHeaders(project);
        }
        return Arrays.asList(
                connectPortType.mc_filter_get_issue_headers(USERNAME, PASSWORD, project.getId(), filter.getId(),
                        BigInteger.ZERO, BigInteger.valueOf(-1)));
    }

    @Nonnull
    IssueData getIssue(@Nonnull IssueHeaderData headerData) throws RemoteException {
        return connectPortType.mc_issue_get(USERNAME, PASSWORD, headerData.getId());
    }

    void addNote(@Nonnull IssueData issue, @Nonnull String noteText) throws RemoteException {
        IssueNoteData note = new IssueNoteData();
        note.setText(noteText);
        addNote(issue, note);
    }

    void addNote(@Nonnull IssueData issue, @Nonnull IssueNoteData note) throws RemoteException {
        connectPortType.mc_issue_note_add(USERNAME, PASSWORD, issue.getId(), note);
    }

    BigInteger addIssue(@Nonnull ProjectData project, @Nonnull IssueData issue) throws RemoteException {
        issue.setProject(new ObjectRef(project.getId(), project.getName()));

        BigInteger id = connectPortType.mc_issue_add(USERNAME, PASSWORD, issue);
        issue.setId(id);
        return id;
    }

    void addRelation(@Nonnull IssueData issue1, @Nonnull IssueData issue2) throws RemoteException {
        RelationshipData relation = new RelationshipData();
        relation.setTarget_id(issue2.getId());
        relation.setType(new ObjectRef(BigInteger.valueOf(1L), null));
        connectPortType.mc_issue_relationship_add(USERNAME, PASSWORD, issue1.getId(), relation);
    }
}
