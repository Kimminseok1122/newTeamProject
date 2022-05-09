package com.teamproject.myweb.Controller;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.teamproject.myweb.command.CommentVO;
import com.teamproject.myweb.command.freeBoardVO;
import com.teamproject.myweb.comment.CommentService;
import com.teamproject.myweb.freeBoard.FreeBoardService;
import com.teamproject.myweb.util.Criteria;
import com.teamproject.myweb.util.PageVO;
import com.teamproject.myweb.command.DebateVO;
import com.teamproject.myweb.debate.DebateService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.teamproject.myweb.Service.boardService;
import com.teamproject.myweb.command.Review_uploadVO;
import com.teamproject.myweb.command.reviewVO;
import com.teamproject.myweb.util.review_Criteria;
import com.teamproject.myweb.util.review_pageVO;


@Controller
@RequestMapping("/board")
public class boardController {
	
	@Autowired
	@Qualifier("freeboardService")
	private FreeBoardService freeBoardService; 
	
	@Autowired
	@Qualifier("CommentService")
	private CommentService commentService;
	
	@Autowired
	@Qualifier("debateService")
	private DebateService debateService;

	@Autowired
	private boardService boardservice;


	
	@GetMapping("/freeBoard")
	public String freeBoard(Model model, Criteria cri, RedirectAttributes RA) {
		
		PageVO pageVO = new PageVO(cri, freeBoardService.getTotal(cri));
		cri.setLeftpage((cri.getPage() -1) * cri.getAmount());
		ArrayList<freeBoardVO> list = freeBoardService.getList(cri);
		
		if(freeBoardService.getTotal(cri) == 0) {
			RA.addFlashAttribute("msg", "검색 결과가 없습니다");
			return "redirect:/board/freeBoard";
		}
		
		model.addAttribute("list", list);
		model.addAttribute("pageVO", pageVO);
		return "board/freeBoard";
	}

	@PostMapping("/freeRegForm")
	public String freeRegForm(freeBoardVO vo, RedirectAttributes RA) {
		
		
		int result = freeBoardService.regist(vo);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "게시글이 등록되었습니다");
		} else {
			RA.addFlashAttribute("msg", "게시글 등록에 실패했습니다");
		}
		
		
		return "redirect:/board/freeBoard";
	}
	
	@GetMapping("/freeBoardReg")
	public String freeBoardReg() {
		
		
		return "board/freeBoardReg";
	}
	
	@GetMapping("/freeBoardDetail")
	public String freeBoardDetail(@RequestParam("free_bno") int free_bno, Model model) {
		
		freeBoardVO detail = freeBoardService.getDetail(free_bno);
		ArrayList<CommentVO> commentVO = commentService.commentList(free_bno);
		int total = commentService.commentTotal(free_bno);
		
		model.addAttribute("detail", detail);
		model.addAttribute("commentVO", commentVO);
		model.addAttribute("total", total);
		
		return "board/freeBoardDetail";
	}
	
	@GetMapping("/freeBoardUpdate")
	public String freeBoardUpdate(@RequestParam("free_bno") int free_bno, Model model) {
			
		freeBoardVO detail = freeBoardService.getDetail(free_bno);
		model.addAttribute("detail", detail);
		
		return "board/freeBoardUpdate";
	}
	@PostMapping("/freeBoardUpdateForm")
	public String freeBoardUpdateForm(freeBoardVO vo,
								  RedirectAttributes RA) {
			
		int result = freeBoardService.update(vo);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "게시글이 수정되었습니다");
		}else {
			RA.addFlashAttribute("msg", "게시글 수정에 실패했습니다. 관리자에게 문의하세요");
		}
		
		return "redirect:/board/freeBoard";
	}
	
	@PostMapping("/freeBoardDelete")
	public String freeBoardDelete(@RequestParam("free_bno") int free_bno,
								  RedirectAttributes RA) {
	
		int result = freeBoardService.delete(free_bno);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "게시글이 삭제되었습니다");
		} else {
			RA.addFlashAttribute("msg", "게시글 삭제에 실패했습니다. 관리자에게 문의하세요");
		}
		return "redirect:/board/freeBoard";
	}
	
	@GetMapping("/commentReg")
	public String commentReg(CommentVO vo, RedirectAttributes RA) {
		
		int result = commentService.commentReg(vo);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "댓글이 등록되었습니다");
		} else {
			RA.addFlashAttribute("msg", "댓글 등록에 실패했습니다");
		}
	
		return "redirect:/board/freeBoardDetail?free_bno=" + vo.getFree_bno();
	}
	
	@GetMapping("/commentDelete")
	public String commentDelete(@RequestParam("cno") int cno,
								@RequestParam("free_bno") int free_bno,
								RedirectAttributes RA) {
		
			int result = commentService.commentDelete(cno);
			
			if(result == 1) {
				RA.addFlashAttribute("msg", "댓글이 삭제되었습니다");
			} else {
				RA.addFlashAttribute("msg", "댓글 삭제에 실패했습니다. 관리자에게 문의하세요");
			}
		
		return "redirect:/board/freeBoardDetail?free_bno=" + free_bno ;
	}
	

	@GetMapping("/reviewBoard")
	public String reviewBoard(Model model, review_Criteria cri) {

		
		
		cri.setLimitLeft((cri.getPage()-1)*cri.getAmount() );
		
		review_pageVO vo = new review_pageVO(cri , boardservice.reviewTotal(cri));
		
		ArrayList<reviewVO> reviewList =  boardservice.reviewList(cri);
		

		
		model.addAttribute("pageVO", vo);
		model.addAttribute("reviewVO", reviewList);
		return "board/reviewBoard";
	}
	
	
	@GetMapping("/reviewReg")
	public String reivewReg() {
		return "board/reviewReg";
	}
	
	@GetMapping("/reviewUpdate")
	public String reviewUpdate(@RequestParam("review_no") int review_no, Model model) {
		
		reviewVO vo =  boardservice.getDetail(review_no);
		ArrayList<Review_uploadVO> list = boardservice.getImg(review_no);
		
		model.addAttribute("UpdateVO", vo);
		model.addAttribute("Imglist", list);
		
		return "board/reviewUpdate";
	}
	
	@PostMapping("/reviewModfiy")
	public String reviewModfiy(reviewVO vo, RedirectAttributes RA) {
		System.out.println(vo.toString());
		
		int result = boardservice.updateReview(vo);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "수정성공");
		} else {
			RA.addFlashAttribute("msg", "수정실패");
		}
		
		return "redirect:/board/reviewBoard";
	}
	
	
	@PostMapping("/reviewDetele")
	public String reviewDetele(@RequestParam("review_no") int review_no,RedirectAttributes RA) {
		
		int result = boardservice.deleteReview(review_no);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "삭제성공");
		} else {
			RA.addFlashAttribute("msg", "삭제실패");
		}
		
		return "redirect:/board/reviewBoard";
	}
	
	@GetMapping("/reviewDetail")
	public String reviewdetail(@RequestParam("review_no") int review_no, Model model) {
		
		
		reviewVO vo =  boardservice.getDetail(review_no);
		ArrayList<Review_uploadVO> list = boardservice.getImg(review_no);
		
		model.addAttribute("detailVO", vo);
		model.addAttribute("Imglist", list);
		return "board/reviewDetail";
	}
	
	@PostMapping("/reviewForm")
	public String reviewform(reviewVO vo,Model model, RedirectAttributes RA,@RequestParam("file") List<MultipartFile> list) {
		
		list = list.stream().filter( f -> f.isEmpty() == false).collect( Collectors.toList());
		
		for(MultipartFile f : list) {
			if(f.getContentType().contains("image") == false) {
				RA.addFlashAttribute("msg","이미지파일을 넣으세요");
				return "redirect:/board/reviewBoard";
			}
		}
		
		int result = boardservice.reviewRegist(vo,list);
		
		if(result == 1) {
			RA.addFlashAttribute("msg", "등록성공");
		} else {
			RA.addFlashAttribute("msg", "등록실패");
		}
		
		return "redirect:/board/reviewBoard";
	}
	
	@GetMapping("/debateBoard")
	public String debateBoard(Model model) {
		
		ArrayList<DebateVO>list = debateService.getList();
		
		model.addAttribute("list", list);
		
		return "board/debateBoard";
	}
	
	@GetMapping("/debateReg")
	public String debateReg() {
		
		return "board/debateReg";
	}
	
	@PostMapping("/debateForm")
	public String debateForm(DebateVO vo,
							 RedirectAttributes RA) {
		int result =  debateService.regist(vo);
		System.out.println(result);
		
		return "redirect:/board/debateBoard";
	}
	
	@GetMapping("/debateUpdate")
	public String debateUpdate(Model model,
							   @RequestParam("debate_no") int debate_no) {
		
		DebateVO debateVO = debateService.getDetail(debate_no);
		model.addAttribute("debateVO", debateVO);
		System.out.println(debateVO.toString());
		
//		ArrayList<DebateVO> list = debateService.getList();
//		model.addAttribute("list", list);
		
		return "board/debateUpdate";
	}
	
	@PostMapping("/updateForm")
	public String updateForm(DebateVO vo,
							 RedirectAttributes RA) {
		
		int result = debateService.update(vo);
		
		
		return "redirect:/board/debateBoard";
		
	}
	
	@PostMapping("/deleteForm")
	public String deleteForm(@RequestParam("debate_no") int debate_no,
							 RedirectAttributes RA) {
		
		int result = debateService.delete(debate_no);
		
		return "redirect:/board/debateBoard";
		
	}

	
	
}
