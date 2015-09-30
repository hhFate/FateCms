package fate.webapp.blog.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;

import fate.webapp.blog.model.MonitorInfoBean;
import fate.webapp.blog.service.MonitorService;
import fate.webapp.blog.utils.Bytes;

/**
 * 
 * 获取系统信息的业务逻辑实现类.
 * 
 * @author GuoHuang
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    
    private static final Logger LOG = Logger.getLogger(MonitorServiceImpl.class);
    
    private static final int CPUTIME = 30;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;
    private static String linuxVersion = null;
    /**
     * 获得当前的监控对象.
     * 
     * @return 返回构造好的监控对象
     * @throws Exception
     * @author GuoHuang
     */
    public MonitorInfoBean getMonitorInfoBean() throws Exception {
        int kb = 1024;
        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 操作系统
        String osName = System.getProperty("os.name");
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb;
        // 获得线程总数
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());
        int totalThread = parentThread.activeCount();
        double cpuRatio = 0;
        
     // 构造返回对象
        MonitorInfoBean infoBean = new MonitorInfoBean();
        infoBean.setOsName(osName);
        
        
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
            infoBean.setCpuRatio(cpuRatio);
            infoBean.setTotalMemory(totalMemory);
            infoBean.setTotalMemorySize(totalMemorySize);
            infoBean.setFreeMemory(freeMemory);
            infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
            infoBean.setMaxMemory(maxMemory);
            
            infoBean.setTotalThread(totalThread);
            infoBean.setUsedMemory(usedMemory);
        } else {
            cpuRatio = getInfoForLinux(infoBean);
            infoBean.setCpuRatio(cpuRatio);
        }
        
        
        
        return infoBean;
    }
    private static double getInfoForLinux(MonitorInfoBean infoBean) {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat = null;
        linuxVersion = System.getProperty("os.version");
        try {
            Process process = Runtime.getRuntime().exec("top -b -n 1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);
            if (linuxVersion.equals("2.4")) {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                String user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();
                user = user.substring(0, user.indexOf("%"));
                system = system.substring(0, system.indexOf("%"));
                nice = nice.substring(0, nice.indexOf("%"));
                float userUsage = new Float(user).floatValue();
                float systemUsage = new Float(system).floatValue();
                float niceUsage = new Float(nice).floatValue();
                return (userUsage + systemUsage + niceUsage) / 100;
            } else {
                brStat.readLine();
                brStat.readLine();
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                String cpuUsage = tokenStat.nextToken();
                
                //linux中正确的内存使用量
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                String tmp = tokenStat.nextToken();
                long totalMem = Long.parseLong(tmp.substring(0,tmp.length()-1));
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tmp = tokenStat.nextToken();
                long freeMem = Long.parseLong(tmp.substring(0,tmp.length()-1));
                tokenStat.nextToken();
                tmp = tokenStat.nextToken();
                long buffers = Long.parseLong(tmp.substring(0,tmp.length()-1));
                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tokenStat.nextToken();
                tmp = tokenStat.nextToken();
                long cached =  Long.parseLong(tmp.substring(0,tmp.length()-1));
                
                
                
                infoBean.setTotalMemory(totalMem);
                infoBean.setFreeMemory(freeMem);
                infoBean.setUsedMemory(totalMem-freeMem-buffers-cached);
                infoBean.setMemoryRatio((totalMem-freeMem-buffers-cached)*100.0/totalMem);
                
                Float usage = new Float(cpuUsage.substring(0, cpuUsage.indexOf("%")));
                return (1 - usage.floatValue() / 100);
            }
        } catch (IOException ioe) {
            freeResource(is, isr, brStat);
            return 1;
        } finally {
            freeResource(is, isr, brStat);
        }
    }
    private static void freeResource(InputStream is, InputStreamReader isr,
            BufferedReader br) {
        try {
            if (is != null)
                is.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
    }
    /**
     * 获得CPU使用率.
     * 
     * @return 返回cpu使用率
     * @author GuoHuang
     */
    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir") + "//system32//wbem//wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
    /**
     * 读取CPU信息.
     * @param proc
     * @return
     * @author GuoHuang
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                String s1 = Bytes.substring(line, kmtidx, rocidx - 1).trim();
                String s2 = Bytes.substring(line, umtidx, wocidx - 1).trim();
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    if (s1.length() > 0)
                        idletime += Long.valueOf(s1).longValue();
                    if (s2.length() > 0)
                        idletime += Long.valueOf(s2).longValue();
                    continue;
                }
                if (s1.length() > 0)
                    kneltime += Long.valueOf(s1).longValue();
                if (s2.length() > 0)
                    usertime += Long.valueOf(s2).longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
