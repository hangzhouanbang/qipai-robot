package com.anbang.qipai.robot.websocket.vo.paodekuai;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import com.dml.puke.pai.DianShu;
import com.dml.puke.pai.PukePaiMian;
import com.dml.puke.wanfa.dianshu.dianshuzu.DianShuZu;

/**
 * 打牌点数方案
 * 
 * @author Neo
 *
 */
public class DaPaiDianShuSolution {

	/**
	 * 牌张数，点数序号 二维索引
	 */
	private static int[][] idxArray = new int[][] { { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47 },
			{ 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113 },
			{ 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191 },
			{ 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263 } };

	private DianShuZu dianShuZu;
	private DianShu[] dachuDianShuArray;
	private String dianshuZuheIdx;
	private Set<PukePaiMian> bichuPai = new HashSet<>();

	public void calculateDianshuZuheIdx() {
		//转换成点数数量数组
		int[] dianshuCountArray = new int[15];
		for (int i = 0; i < dachuDianShuArray.length; i++) {
			DianShu dianShu = dachuDianShuArray[i];
			dianshuCountArray[dianShu.ordinal()]++;
		}

		//按牌的点数遍历，根据数量取坐标
		long l = 1;
		BigInteger bi = null;
		for (int i = 0; i < 15; i++) {
			int dianshuCount = dianshuCountArray[i];
			if (dianshuCount > 0) {
				int idx = idxArray[dianshuCount - 1][i];
				if (bi == null) {
					long nl = l * idx;
					if (nl < l) {// 越乘越小说明爆掉了
						bi = BigInteger.valueOf(l).multiply(BigInteger.valueOf(idx));
					} else {
						l = nl;
					}
				} else {
					bi = bi.multiply(BigInteger.valueOf(idx));
				}
			}
		}
		if (bi == null) {
			dianshuZuheIdx = String.valueOf(l);
		} else {
			dianshuZuheIdx = bi.toString();
		}
	}

	public DianShuZu getDianShuZu() {
		return dianShuZu;
	}

	public void setDianShuZu(DianShuZu dianShuZu) {
		this.dianShuZu = dianShuZu;
	}

	public DianShu[] getDachuDianShuArray() {
		return dachuDianShuArray;
	}

	public void setDachuDianShuArray(DianShu[] dachuDianShuArray) {
		this.dachuDianShuArray = dachuDianShuArray;
	}

	public String getDianshuZuheIdx() {
		return dianshuZuheIdx;
	}

	public void setDianshuZuheIdx(String dianshuZuheIdx) {
		this.dianshuZuheIdx = dianshuZuheIdx;
	}

	public Set<PukePaiMian> getBichuPai() {
		return bichuPai;
	}

	public void setBichuPai(Set<PukePaiMian> bichuPai) {
		this.bichuPai = bichuPai;
	}

	@Override
	public int hashCode() {
		return dianshuZuheIdx.hashCode() + dianShuZu.hashCode() * 10;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DaPaiDianShuSolution other = (DaPaiDianShuSolution) obj;
		if (dianshuZuheIdx == null) {
			if (other.dianshuZuheIdx != null)
				return false;
		} else if (!dianshuZuheIdx.equals(other.dianshuZuheIdx))
			return false;
		if (dianShuZu == null) {
			if (other.dianShuZu != null)
				return false;
		} else if (!dianShuZu.equals(other.dianShuZu))
			return false;
		return true;
	}

}
