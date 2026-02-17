package Tools.JWT.JwtMapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface keyMapper {

    /**
     * 根据密钥版本查询密钥
     *
     * @param KeyVersion 密钥版本
     * @return 密钥值
     */
    @Select("select KeyValue from jwtKey where KeyVersion = #{KeyVersion}")
    String selectKeyByVersion(@Param("KeyVersion") String KeyVersion);

    /**
     * 插入新的密钥
     *
     * @param newKeyVersion 新的密钥版本
     * @param s             新的密钥值
     */
    @Insert("insert into jwtKey (KeyVersion, KeyValue) values (#{newKeyVersion}, #{s})")
    void insertKeyByVersion(@Param("newKeyVersion") String newKeyVersion, @Param("s") String s);

    /**
     * 根据密钥版本删除密钥
     *
     * @param oldKeyVersion 旧的密钥版本
     */
    @Delete("delete from jwtKey where KeyVersion = #{oldKeyVersion}")
    void deleteKeyByVersion(@Param("oldKeyVersion") String oldKeyVersion);

    /**
     * 根据创建时间查询最新的密钥版本
     *
     * @return 最新的密钥版本
     */
    @Select("select KeyVersion from jwtKey order by CreateTime limit 1;")
    String selectKeyVersionByCreateTime();
}
