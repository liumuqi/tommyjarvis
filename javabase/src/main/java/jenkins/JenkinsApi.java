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

@Component
public class JenkinsApi {
    //    private static Map<Job.JobTypeEnum, List<String>> typeParams = new HashMap<>();
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
     * @param cascadingJobs 保存关联job
     * @param job           根
     */
    public void cascadingJob(List<JobWithDetails> cascadingJobs, JobWithDetails job) {
        cascadingJobs.add(job);
        List<Job> downstreamProjects = job.getDownstreamProjects();
        if (downstreamProjects != null && downstreamProjects.size() > 0) {
            downstreamProjects.stream().forEachOrdered(jd -> {
                try {
                    JobWithDetails details = jd.details();
                    cascadingJob(cascadingJobs, details);
                } catch (IOException e) {
                }
            });
        }
    }

    /**
     * 抓取 这个jobId 下的所有子job
     * 注意: 因每次创建job的jobId不一样，所以级联的子job都会有相同的jobId,这样可以通过jobId 把子job的 每个build(因jobId唯一,build 也就唯一了) 都拉出来
     *
     * @param jobId
     * @param cascadingJobs
     * @return
     */
    public List<Build> fetchAllBuildJob(Job jobTypeEnum, Long jobId, List<JobWithDetails> cascadingJobs) {
        List<Build> cascadingBuildList = new ArrayList<>(cascadingJobs.size());
        cascadingJobs.stream().forEachOrdered(cj -> {
            boolean retry = true;
            int pageSize = 20;
            int curr = 0;
            List<Build> allBuilds = new ArrayList<>();
            boolean contains = false;
            do {
                try {
                    allBuilds = getAllBuilds(cj, curr * pageSize, (++curr) * pageSize);
                    for (int i = 0; i < allBuilds.size(); i++) {
                        Build build = allBuilds.get(i);
                        BuildWithDetails details = build.details();
//                        Long jid = checkBuild(jobTypeEnum).apply(details);
//                        if (jid != null && jid.equals(jobId)) {
//                            cascadingBuildList.add(build);
//                            contains = true;
//                            retry = false;
//                            break;
//                        }
//                        if (jid == null || jobId.compareTo(jid) > 0) {
//                            contains = false;
//                            retry = false;
//                            break;
//                        }
                    }
                } catch (IOException e) {
                }
            } while (!contains && allBuilds.size() > 0 && retry);
            if (!contains) {
                cascadingBuildList.add(null);
            }
        });
        return cascadingBuildList;
    }

    private List<Build> getAllBuilds(JobWithDetails jd, int fromindex, int to) throws IOException {
        Range range = Range.build().from(fromindex).to(to);
        List<Build> allBuilds = jd.getAllBuilds(range);
        return allBuilds;
    }

    private Function<BuildWithDetails, Long> checkBuild(Job jobTypeEnum) {
        return detail -> {
            List<HashMap<String, List<HashMap>>> actions = detail.getActions();
            Optional<HashMap<String, List<HashMap>>> mapOptional = actions != null ?
                    actions.stream().filter(a -> a.containsKey("parameters")).findFirst() : Optional.empty();
//            Set<String> set = mapOptional.map(m -> m.get("parameters").parallelStream().map(
//                    p -> {
//                        if (p.containsKey("name") && typeParams.get(jobTypeEnum).stream().anyMatch(String.valueOf(p.get("name"))::equalsIgnoreCase)) {
//                            return String.valueOf(p.get("value"));
//                        }
//                        return "";
//                    }
//            ).collect(Collectors.toSet())).orElse(Collections.emptySet());
//            String jid = set.stream().filter(s -> StringUtils.isNotBlank(s)).findFirst().orElse(null);
//            Long value = jid == null ? null : Long.valueOf(jid);
//            return value;
            return null;
        };
    }

    /**
     * jenkins 排队的任务
     *
     * @param cascadingJobs
     * @param jobId
     */
    public List<QueueItem> fetchAllJobQueue(String jobId, List<JobWithDetails> cascadingJobs) {
        List<String> jobNames = cascadingJobs.parallelStream().map(j -> j.getName()).collect(Collectors.toList());
        Queue queue = null;
        try {
            queue = jenkinsServer.getQueue();
        } catch (IOException e) {
        }
        List<QueueItem> items = queue.getItems();
        List<QueueItem> list = items.parallelStream().filter(q -> {
            if (jobNames.contains(q.getTask().getName())) {
                String params = q.getParams();
                if (StringUtils.isNotBlank(params) && params.indexOf("job_id=" + jobId) > 0) {
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
