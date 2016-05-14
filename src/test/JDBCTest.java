package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.eqsys.dao.StatusDataDao;
import com.eqsys.msg.data.StatusData;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.SysConfig;

public class JDBCTest {

	public static void main(String[] args) {
		SysConfig.preConfig();
		JDBCHelper.initDB();
		//testJDBConnection();
		getStatusData();
	}
	
	/** 获取statusdata_t 数据 */
	private static void getStatusData() {
		List<StatusData> list = StatusDataDao.getRecord("18373", 1463178267544l, 1463178297518l);
		System.err.println("list size:"+list.size());
		if(list.size() != 0){
			StatusData data = list.get(0);
			System.err.println("id:"+data.getId());
			System.err.println("duration:"+data.getDur());
			System.err.println("starttime:"+data.getStartTime());
			System.err.println("ew:"+data.getEwPeakValue()[0]);
			System.err.println("ns:"+data.getNsPeakValue()[0]);
			System.err.println("ud:"+data.getUdPeakValue()[0]);
		}
	}

	/** 测试一个数据库连接是否能用两次 */
	public static void testJDBConnection(){
		String sql1 = "select stationid from clientinfo_t where stationid=?";
		String sql2 = "insert into  clientinfo_t(stationid, permit) values( ?, ?);";
		PreparedStatement preStat = null;
		PreparedStatement preStat2 = null;
		Connection conn = null;
			try {
				conn = JDBCHelper.getDBConnection();
				preStat = conn.prepareStatement(sql1);
				preStat.setString(1, "00000");
				System.out.println(preStat.toString());
				ResultSet result = preStat.executeQuery();
				if(!result.next()){
					System.out.println("sql1 result is ok");
					preStat2 = conn.prepareStatement(sql2);
					preStat2.setString(1, "00000");
					preStat2.setShort(2, (short) 5);
					int result2 = preStat2.executeUpdate();
					System.out.println("sql2 result2:"+result2);
				}else{
					System.out.println("sql1 result is empty");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (preStat != null) {
						preStat.close();
					}
					if (preStat2 != null) {
						preStat.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JDBCHelper.closeDBConnection(conn);
			}
	}

}
