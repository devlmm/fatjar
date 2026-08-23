package com.workspace.fatjar.generator;

/**
 * 代码生成器配置持有类（集中存放所有可配置常量）
 * <p>
 * 职责：
 *   1. 集中维护数据库连接信息（URL / 用户名 / 密码）
 *   2. 集中维护生成代码的输出目录、包路径、模块名
 *   3. 集中维护策略配置相关常量（逻辑删除字段、表前缀过滤等）
 *   4. 提供默认值，保证 {@link FatjarCodeGenerator#main(String[])} 开箱即用
 * <p>
 * 修改建议：
 *   开发者可按需修改本类中的常量，或通过 main 方法参数动态覆盖，避免硬编码到主类逻辑中。
 * <p>
 * 用法示例：
 *   <pre>
 *   // 直接修改常量值即可，例如指向测试库：
 *   public static final String DB_URL = "jdbc:mysql://192.168.1.100:3306/fatjar_test?...";
 *   // 然后运行 FatjarCodeGenerator.main 即可生成到 GeneratorConfig.OUTPUT_DIR 指定的目录
 *   </pre>
 *
 * @author fatjar
 * @since 1.0.0
 */
public final class GeneratorConfig {

    /**
     * 私有构造方法，禁止实例化（纯常量持有类）
     */
    private GeneratorConfig() {
        throw new UnsupportedOperationException("配置常量类不允许实例化");
    }

    /* ====================================================================== */
    /* ========================== 数据库连接配置 ============================= */
    /* ====================================================================== */

    /**
     * 数据库 JDBC URL
     * <p>
     * 默认连接本地 fatjar 库，编码 UTF-8，时区 Asia/Shanghai。
     * 修改指向其他库时，注意同步调整下方用户名、密码。
     */
    public static final String DB_URL =
            "jdbc:mysql://localhost:3306/fatjar?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";

    /** 数据库用户名（默认 root，生产请改为业务账号） */
    public static final String DB_USERNAME = "root";

    /** 数据库密码（默认 root，生产请改为强密码并使用环境变量注入） */
    public static final String DB_PASSWORD = "root";

    /* ====================================================================== */
    /* ========================== 代码生成配置 =============================== */
    /* ====================================================================== */

    /**
     * 代码生成输出根目录
     * <p>
     * 默认输出到当前模块的 src/main/java 下，便于直接进入版本控制。
     * 如需输出到其他位置，可改为绝对路径，如 "D:/generated-code"。
     */
    public static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/java";

    /**
     * XML Mapper 输出目录
     * <p>
     * MyBatis Mapper XML 默认放在 src/main/resources 下与 Java 包路径对应的位置，
     * 这样 Spring Boot 可通过 classpath 星号 mapper 星星点星点 xml 模式自动扫描
     * （注意：注释中不能直接写 classpath 星号 mapper 斜杠星星 斜杠星点 xml，因 斜杠星 会终止块注释）。
     */
    public static final String XML_OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/resources";

    /** 作者名（写入所有生成类的 @author 注释） */
    public static final String AUTHOR = "fatjar";

    /** 父包名（所有生成代码的根包，与项目 GroupId 一致） */
    public static final String PARENT_PACKAGE = "com.workspace.fatjar";

    /**
     * 业务模块名（决定子包与输出目录）
     * <p>
     * 例如 moduleName=erp 时，生成包路径为：
     *   com.workspace.fatjar.erp.entity / .mapper / .service / .controller
     * <p>
     * 多业务模块（auth/erp/oa/crm/ems）按需切换此值后重新生成。
     */
    public static final String MODULE_NAME = "erp";

    /* ====================================================================== */
    /* ========================== 包名配置 =================================== */
    /* ====================================================================== */

    /** Entity 实体类子包名 */
    public static final String PACKAGE_ENTITY = "entity";

    /** Mapper 接口子包名 */
    public static final String PACKAGE_MAPPER = "mapper";

    /** Service 接口子包名 */
    public static final String PACKAGE_SERVICE = "service";

    /** Service 实现类子包名（默认 service.impl） */
    public static final String PACKAGE_SERVICE_IMPL = "service.impl";

    /** Controller 控制器子包名 */
    public static final String PACKAGE_CONTROLLER = "controller";

    /** Mapper XML 文件子目录名 */
    public static final String PACKAGE_XML = "mapper";

    /* ====================================================================== */
    /* ========================== 策略配置 =================================== */
    /* ====================================================================== */

    /**
     * 需要生成的表名列表（逗号分隔）
     * <p>
     * 默认为空字符串表示生成全部表；如需指定，填表名并以英文逗号分隔，
     * 例如："t_user,t_dept,t_role"。
     */
    public static final String TABLE_NAMES = "";

    /**
     * 需要过滤的表前缀（逗号分隔）
     * <p>
     * 生成实体类时会自动去除这些前缀，例如表名 t_user 过滤 t_ 后生成 User.java。
     * 默认过滤 fatjar 项目的通用前缀。
     */
    public static final String TABLE_PREFIX = "t_,sys_";

    /**
     * 逻辑删除字段名
     * <p>
     * 与 {@code com.workspace.fatjar.common.entity.BaseEntity#deleted} 保持一致，
     * MyBatis-Plus 会自动在 SQL 中追加 deleted=0 过滤条件。
     */
    public static final String LOGIC_DELETE_FIELD = "deleted";

    /**
     * 乐观锁字段名
     * <p>
     * 若表中存在 version 字段则启用乐观锁，不存在时此项不影响生成。
     */
    public static final String VERSION_FIELD = "version";
}
