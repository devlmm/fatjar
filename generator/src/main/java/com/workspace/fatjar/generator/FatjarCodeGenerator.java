package com.workspace.fatjar.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Fatjar 代码生成器主类（基于 MyBatis-Plus 3.5.5 FastAutoGenerator）
 * <p>
 * 职责：
 *   1. 读取数据库表结构，反向生成 Entity / Mapper / Mapper.xml / Service / ServiceImpl / Controller
 *   2. 输出位置、包名、策略全部来自 {@link GeneratorConfig} 常量，开箱即用
 *   3. 支持通过 main 方法参数动态覆盖默认配置，避免反复修改源码
 * <p>
 * 生成规范（与项目工程结构对齐）：
 *   - 实体类使用 Lombok 链式模型：{@code @Accessors(chain = true)} + {@code @Data}
 *   - 实体类携带 {@code @TableName}，字段携带 {@code @TableField}（仅当命名不规则时）
 *   - 主键策略 {@link IdType#ASSIGN_ID}（雪花算法，与 BaseEntity 对齐）
 *   - 逻辑删除字段 {@code deleted}，与 {@code BaseEntity#deleted} 一致
 *   - Mapper 接口标注 {@code @Mapper} 注解，可直接被 Spring 扫描
 *   - Controller 标注 {@code @RestController}，返回 JSON
 *   - 时间字段使用 {@link java.time.LocalDate} / {@link java.time.LocalDateTime}
 *   - 模板引擎采用 Velocity（MyBatis-Plus 默认推荐）
 * <p>
 * 用法示例：
 *   <pre>
 *   // 1. 默认参数（使用 GeneratorConfig 中常量，生成所有表到默认目录）：
 *   FatjarCodeGenerator.main();
 *
 *   // 2. 指定表名（覆盖 GeneratorConfig.TABLE_NAMES）：
 *   FatjarCodeGenerator.main("t_user,t_dept");
 *
 *   // 3. 指定表名 + 模块名（覆盖 GeneratorConfig.MODULE_NAME）：
 *   FatjarCodeGenerator.main("t_user,t_dept", "auth");
 *
 *   // 4. 完整覆盖（表名 + 模块名 + DB URL + 用户名 + 密码）：
 *   FatjarCodeGenerator.main(
 *       "t_user,t_dept",                       // 表名（逗号分隔）
 *       "auth",                                 // 模块名
 *       "jdbc:mysql://192.168.1.10:3306/fatjar", // DB URL
 *       "dev_user",                             // 用户名
 *       "dev_pwd"                               // 密码
 *   );
 *   </pre>
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
public class FatjarCodeGenerator {

    /**
     * 程序入口方法
     * <p>
     * 参数顺序及含义（全部可选，按位置匹配，缺省时使用 {@link GeneratorConfig} 默认值）：
     * <ul>
     *   <li>args[0]: 表名列表，逗号分隔（如 "t_user,t_dept"）</li>
     *   <li>args[1]: 业务模块名（如 "erp" / "auth" / "oa" / "crm" / "ems"）</li>
     *   <li>args[2]: 数据库 JDBC URL</li>
     *   <li>args[3]: 数据库用户名</li>
     *   <li>args[4]: 数据库密码</li>
     * </ul>
     *
     * @param args 命令行参数，详见方法说明；可为空数组
     */
    public static void main(String[] args) {
        // ---------- 解析命令行参数，未传入则使用 GeneratorConfig 默认值 ----------
        String tableNames = args.length > 0 && !args[0].isEmpty() ? args[0] : GeneratorConfig.TABLE_NAMES;
        String moduleName = args.length > 1 && !args[1].isEmpty() ? args[1] : GeneratorConfig.MODULE_NAME;
        String dbUrl = args.length > 2 && !args[2].isEmpty() ? args[2] : GeneratorConfig.DB_URL;
        String dbUsername = args.length > 3 && !args[3].isEmpty() ? args[3] : GeneratorConfig.DB_USERNAME;
        String dbPassword = args.length > 4 && !args[4].isEmpty() ? args[4] : GeneratorConfig.DB_PASSWORD;

        log.info("===== FatjarCodeGenerator 启动 =====");
        log.info("数据库 URL  : {}", dbUrl);
        log.info("数据库用户  : {}", dbUsername);
        log.info("业务模块名  : {}", moduleName);
        log.info("生成表名    : {}", tableNames.isEmpty() ? "（全部表）" : tableNames);
        log.info("Java 输出目录: {}", GeneratorConfig.OUTPUT_DIR);
        log.info("XML  输出目录: {}", GeneratorConfig.XML_OUTPUT_DIR);
        log.info("=====================================");

        // ---------- 解析表名与表前缀为数组，供策略配置使用 ----------
        String[] tables = tableNames.isEmpty() ? new String[]{} : tableNames.split(",");
        String[] tablePrefixes = GeneratorConfig.TABLE_PREFIX.isEmpty()
                ? new String[]{} : GeneratorConfig.TABLE_PREFIX.split(",");

        // ---------- 构建 XML Mapper 输出路径映射 ----------
        // key = OutputFile.xml 表示这是 Mapper XML 的输出位置；value = 绝对目录
        Map<OutputFile, String> pathInfo = new HashMap<>(4);
        pathInfo.put(OutputFile.xml, GeneratorConfig.XML_OUTPUT_DIR);

        // ---------- 启动 FastAutoGenerator 并链式配置 ----------
        FastAutoGenerator.create(dbUrl, dbUsername, dbPassword)
                // ====== 1. 全局配置 ======
                .globalConfig(globalBuilder -> globalBuilder
                        .author(GeneratorConfig.AUTHOR)             // 作者写入类注释 @author fatjar
                        .outputDir(GeneratorConfig.OUTPUT_DIR)     // 生成代码输出根目录
                        .disableOpenDir()                          // 禁止生成后自动打开资源管理器
                        .dateType(DateType.TIME_PACK)               // 使用 java.time 包（LocalDate/LocalDateTime）
                        .commentDate("yyyy-MM-dd")                  // 注释中的日期格式
                )
                // ====== 2. 包配置 ======
                .packageConfig(packageBuilder -> packageBuilder
                        .parent(GeneratorConfig.PARENT_PACKAGE)            // 父包：com.workspace.fatjar
                        .moduleName(moduleName)                            // 子模块名：erp 等
                        .entity(GeneratorConfig.PACKAGE_ENTITY)             // 实体子包
                        .mapper(GeneratorConfig.PACKAGE_MAPPER)             // Mapper 子包
                        .service(GeneratorConfig.PACKAGE_SERVICE)          // Service 子包
                        .serviceImpl(GeneratorConfig.PACKAGE_SERVICE_IMPL)  // ServiceImpl 子包
                        .controller(GeneratorConfig.PACKAGE_CONTROLLER)    // Controller 子包
                        .xml(GeneratorConfig.PACKAGE_XML)                   // Mapper XML 子目录
                        .pathInfo(pathInfo)                                  // 自定义输出路径
                )
                // ====== 3. 策略配置 ======
                .strategyConfig(strategyBuilder -> {
                    // 3.1 通用策略：表名包含范围 + 表前缀过滤
                    if (tables.length > 0) {
                        strategyBuilder.addInclude(tables);
                    }
                    if (tablePrefixes.length > 0) {
                        strategyBuilder.addTablePrefix(tablePrefixes);
                    }
                    // 3.2 实体策略：Lombok 链式 + @TableName + 逻辑删除 + 乐观锁 + 雪花主键
                    strategyBuilder.entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)         // 表名下划线转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel)   // 字段名下划线转驼峰
                            .enableChainModel()                                  // @Accessors(chain = true) 链式 setter
                            .enableLombok()                                      // 启用 Lombok @Data
                            .enableTableFieldAnnotation()                        // 启用 @TableField 注解
                            .enableActiveRecord()                                // 启用 ActiveRecord 模式
                            .idType(IdType.ASSIGN_ID)                            // 主键雪花算法（与 BaseEntity 对齐）
                            .logicDeleteColumnName(GeneratorConfig.LOGIC_DELETE_FIELD) // 逻辑删除字段 deleted
                            .versionColumnName(GeneratorConfig.VERSION_FIELD)   // 乐观锁字段 version
                            .enableFileOverride()                                // 覆盖已有文件
                            .formatFileName("%s");                               // 实体类名格式（无 I 前缀）
                    // 3.3 Mapper 策略：@Mapper 注解 + BaseResultMap + BaseColumnList
                    strategyBuilder.mapperBuilder()
                            .enableMapperAnnotation()        // 生成 @Mapper 注解
                            .enableBaseResultMap()           // 生成 BaseResultMap
                            .enableBaseColumnList()          // 生成 BaseColumnList
                            .formatMapperFileName("%sMapper")       // Mapper 接口名格式
                            .formatXmlFileName("%sMapper");         // XML 文件名格式
                    // 3.4 Service 策略：接口名 IService + 实现 ServiceImpl
                    strategyBuilder.serviceBuilder()
                            .formatServiceFileName("I%sService")         // 接口名 IXxxService
                            .formatServiceImplFileName("%sServiceImpl");  // 实现类 XxxServiceImpl
                    // 3.5 Controller 策略：@RestController + 连字符路径
                    strategyBuilder.controllerBuilder()
                            .enableRestStyle()        // 启用 @RestController
                            .enableHyphenStyle()      // URL 使用连字符风格（如 /user-info）
                            .formatFileName("%sController");  // 控制器名格式
                })
                // ====== 4. 模板引擎：Velocity（MyBatis-Plus 默认） ======
                .templateEngine(new VelocityTemplateEngine())
                // ====== 5. 执行生成 ======
                .execute();

        log.info("===== 代码生成完毕，请到 {} 目录查看 =====", GeneratorConfig.OUTPUT_DIR);
    }
}
