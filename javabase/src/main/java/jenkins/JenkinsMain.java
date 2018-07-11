package jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.Range;
import com.offbytwo.jenkins.model.*;
import com.offbytwo.jenkins.model.Queue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author muqi.lmq
 * @desc jenkins操作api 童虎的jenkins有父子关系.约定在typeParams定义的job中的字段为 定位父子任务build关系字段
 * @date 22/08/2017.
 */
@Component
public class JenkinsMain {
    public static final BuildWithDetails BUILDDETAIL = new BuildWithDetails();
    public static final Build BUILD = new Build();
    private static Map<Type, List<String>> typeParams = new HashMap<>();

    static {
        ArrayList<String> common = new ArrayList<>();
        common.add("job_id");
        typeParams.put(Type.A, common);
//        typeParams.put(Type.STREAMING_CALC, common);
//        typeParams.put(Type.TRACE_COMPARE, common);
//        typeParams.put(Type.QUARTER_RECONSTRUCT, common);
//        typeParams.put(Type.HISTORY_RECONSTRUCT, common);
//        typeParams.put(Type.AVSB, common);
//        typeParams.put(Type.STREAMING_PRLOG, common);
//        typeParams.put(Type.TRUE_SET, common);
//        typeParams.put(Type.TRUE_SET_TEST, common);
//        typeParams.put(Type.TRUE_PRLOG, common);
//        ArrayList<String> prlogselect = new ArrayList<>();
//        ArrayList<String> prlogselecttrace = new ArrayList<>();
//        prlogselect.add("condition_id");
//        prlogselecttrace.add("condition_id");
//        typeParams.put(Type.TRUE_PRLOG_SELECT, prlogselect);
//        typeParams.put(Type.TRACE_PRLOG_SELECT, prlogselecttrace);
//
//        ArrayList<String> matchInheritJob = new ArrayList<>();
//        matchInheritJob.add("inherit_jobid");
//        typeParams.put(Type.MATCH_INHERIT, matchInheritJob);
//
//        ArrayList<String> creations = new ArrayList<>();
//        creations.add("jobid");
//        typeParams.put(Type.MATCH_TRUE_CREATION, creations);
//
//        ArrayList<String> matchStreams = new ArrayList<>();
//        matchStreams.add("reg_jobid");
//        typeParams.put(Type.MATCH_STREAMING, matchStreams);
//
//        ArrayList<String> matchIndicators = new ArrayList<>();
//        matchIndicators.add("jobid");
//        typeParams.put(Type.MATCH_CALC_INDICATOR, matchIndicators);
    }

    @Autowired
    private JenkinsServer jenkinsServer;

    /**
     * 获取jobName 对应的jenkins job
     *
     * @param jobName
     * @return
     * @throws IOException
     */
    public JobWithDetails getJob(String jobName) throws IOException {
        JobWithDetails job = jenkinsServer.getJob(jobName);
        return job;
    }

    /**
     * jenkinsjob build
     *
     * @param job
     * @param params
     * @return
     */
    public String jobBuild(JobWithDetails job, Map<String, String> params) {
        try {
            QueueReference buildref = job.build(params, false);
            QueueItem queueItem = jenkinsServer.getQueueItem(buildref);
            return queueItem.getUrl();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 获取当前job的 build
     *
     * @param job     当前job
     * @param buildId 当前buildId
     * @return
     */
    public Build getBuild(JobWithDetails job, Integer buildId) {
        Build build = job.getBuildByNumber(buildId);
        return build;
    }

    /**
     * 抓取jenkins job下的所有关联job
     *
     * @param job 根
     */
    public JenkinsTree<JobWithDetails> fetchJobs(JobWithDetails job) throws IOException {
        JenkinsTree<JobWithDetails> treeJob = new JenkinsTree();
        treeJob.setCurrent(job);
        List<com.offbytwo.jenkins.model.Job> downstreamProjects = job.getDownstreamProjects();
        if (downstreamProjects != null && downstreamProjects.size() > 0) {
            List<JenkinsTree<JobWithDetails>> trees = downstreamProjects.parallelStream().map(d -> {
                JobWithDetails details = null;
                JenkinsTree<JobWithDetails> children = null;
                try {
                    details = d.details();
                    JenkinsTree<JobWithDetails> t_ = new JenkinsTree<>();
                    t_.setCurrent(details);
                    children = fetchJobs(details);
                } catch (IOException e) {
                }
                return children;
            }).collect(Collectors.toList());
            treeJob.setChildren(trees);
        }
        return treeJob;
    }

    <R extends Build> Function<JobWithDetails, Kv<JobWithDetails, R>> processJobToData(Type type, Long jobId, R returnClass) {
        Function<JobWithDetails, Kv<JobWithDetails, R>> function = (cj) -> {
            boolean retry = true;
            int pageSize = 200;
            int curr = 0;
            List<Build> allBuilds = new ArrayList<>();
            boolean contains = false;
            Kv<JobWithDetails, R> kv = new Kv<>();
            kv.setK(cj);
            int checkRebuild = 0;
            do try {
                Range range = Range.build().from(curr * pageSize).to((++curr) * pageSize);
                allBuilds = cj.getAllBuilds(range);
                for (int i = 0; i < allBuilds.size(); i++) {
                    Build build = allBuilds.get(i);
                    BuildWithDetails details = build.details();
                    Long jid = checkBuild(type).apply(details);
                    if (jid != null && jid.equals(jobId)) {
                        contains = true;
                        retry = false;
                        boolean isDetail = returnClass instanceof BuildWithDetails;
                        if (isDetail) {
                            kv.setV((R) details);
                        } else if (returnClass instanceof Build) {
                            kv.setV((R) build);
                        }
                        break;
                    }
                    if (jid == null || jobId.compareTo(jid) > 0) {
                        contains = false;
                        checkRebuild++;
                        if (checkRebuild >= 5) {
                            retry = false;
                            break;
                        }
                    }
                }
            } catch (IOException e) {
            } while (!contains && allBuilds.size() > 0 && retry);
            return kv;
        };
        return function;
    }

    private Function<BuildWithDetails, Long> checkBuild(Type type) {
        return detail -> {
            List<HashMap<String, List<HashMap>>> actions = detail.getActions();
            Optional<HashMap<String, List<HashMap>>> mapOptional = actions != null ?
                    actions.stream().filter(a -> a.containsKey("parameters")).findFirst() : Optional.empty();
            Set<String> set = mapOptional.map(m -> m.get("parameters").parallelStream().map(
                    p -> {
                        if (p.containsKey("name") && typeParams.get(type).stream().anyMatch(String.valueOf(p.get("name"))::equalsIgnoreCase)) {
                            return String.valueOf(p.get("value"));
                        }
                        return "";
                    }
            ).collect(Collectors.toSet())).orElse(Collections.emptySet());
            String jid = set.stream().filter(StringUtils::isNotBlank).findFirst().orElse(null);
            Long value = jid == null ? null : Long.valueOf(jid);
            return value;
        };
    }

    /**
     * 抓取 这个jobId 下的所有子job
     * 注意: 因每次创建job的jobId不一样，所以级联的子job都会有相同的jobId,这样可以通过jobId 把子job的 每个build(因jobId唯一,build 也就唯一了) 都拉出来
     *
     * @param jobId
     * @return
     */
    public JenkinsTree<Kv<JobWithDetails, BuildWithDetails>> fetchAllBuildDetails(Type type, Long jobId, JenkinsTree<JobWithDetails> tree) {
        JenkinsTree<Kv<JobWithDetails, BuildWithDetails>> buildTree = JenkinsTree.walkToOtherTree(tree, processJobToData(type, jobId, BUILDDETAIL), true);
        return buildTree;
    }

    public JenkinsTree<Kv<JobWithDetails, Build>> fetchAllBuild(Type type, Long jobId, JenkinsTree<JobWithDetails> tree) {
        JenkinsTree<Kv<JobWithDetails, Build>> buildTree = JenkinsTree.walkToOtherTree(tree, processJobToData(type, jobId, BUILD), true);
        return buildTree;
    }

    public Kv<JobWithDetails, BuildWithDetails> fetchBuildDetails(Type type, Long jobId, JobWithDetails jobWithDetails) {
        JenkinsTree<JobWithDetails> tree = new JenkinsTree<>();
        tree.setCurrent(jobWithDetails);
        JenkinsTree<Kv<JobWithDetails, BuildWithDetails>> buildTree = JenkinsTree.walkToOtherTree(tree, processJobToData(type, jobId, BUILDDETAIL), true);
        return buildTree == null ? null : buildTree.getCurrent();
    }


    /**
     * jenkins 排队的任务
     *
     * @param jobId
     */
    public List<QueueItem> fetchAllJobQueue(String jobId, Type type, JenkinsTree<JobWithDetails> jobs) {
        List<String> jobNames = JenkinsTree.transToList(jobs).parallelStream().map(Job::getName).collect(Collectors.toList());
        Queue queue = null;
        try {
            queue = jenkinsServer.getQueue();
        } catch (IOException e) {
        }
        List<QueueItem> items = queue.getItems();
        List<QueueItem> list = items.parallelStream().filter(q -> {
            if (jobNames.contains(q.getTask().getName())) {
                String params = q.getParams();
                List<String> checks = typeParams.get(type);
                List<String> checkfilters = checks.stream().map(c -> c + "=" + jobId).collect(Collectors.toList());
                if (StringUtils.isNotBlank(params) && checkfilters.stream().anyMatch(c -> params.indexOf(c) > 0)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return list;
    }

    public boolean cancelQueueJob(QueueItem queueItem) {
        boolean done = false;
        QueueItem item = null;
        try {
            item = jenkinsServer.getQueueItem(new QueueReference(queueItem.getUrl()));
            boolean cancelled = item.isCancelled();
            if (!cancelled) {
                item.getClient().post("/queue/cancelItem?id=" + String.valueOf(item.getId()));
            }
            done = true;
        } catch (IOException e) {
        }
        return done;
    }
}