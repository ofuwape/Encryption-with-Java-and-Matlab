%%%%%%%%%%%%%%%%%%%%%%%%%The rgf function file must be saved in the same
%%%%%%%%%%%%%%%%%%%%%%%%%directory to be able to run this program
%%%%%%%%%The input file is hard coded as input.txt
%%%%%%%%%%The key file is hard coded as key.txt
clc, clear
%Reading the files
fid1 = fopen('key1.txt');
key_text = fscanf(fid1, '%s');
fclose(fid1);
fid = fopen('input1.txt');
input_text = fscanf(fid, '%s');
counter=[];
fclose(fid);
%Removing spaces and punctuation
for count=1:length(input_text)
    if ~isletter(input_text(count))
        counter=[count counter];
    end
end
fh = fopen('output.txt','w');
diary('output.txt');
diary on;
disp('Initial text');
disp(input_text);
input_text(counter)='';
disp('Output after preprocessing');
disp(input_text);
%Substitution
for countx=0:length(input_text)-1
    message = input_text(countx+1) - 65;
    the_key = key_text(mod(countx,length(key_text))+1) - 65;
    input_text(countx+1)=char((mod((message+the_key),26)+65));
end
disp('Output after substitution');
disp(input_text);
%padding
oldlength=(length(input_text));
newl=ceil(length(input_text)/16)*16;   %% calculating the new length of after padding
for counterxx=(oldlength+1):newl
    input_text=[input_text 'A'];
end
disp(input_text);
disp('After padding');
%disp(input_text);

% converting to matrix format
input_mat=[];
eachRow=[];
for counterxxx=1:length(input_text)
    eachRow=[eachRow input_text(counterxxx)];
    if(mod(counterxxx,4)==0)
        if(counterxxx==4)
            input_mat=[input_mat eachRow];
        else
            input_mat=[input_mat; eachRow];
        end
        eachRow=[];
    end
end
%displaying matrix
for i=1:length(input_mat)
    disp(input_mat(i,:));
    if(mod(i,4)==0)
        fprintf('\n');
    end
end

%shifting arrays
shiftvalue=0;
for i=1:length(input_mat)
    if (mod(i,4)==1)
        shiftvalue=0;
    elseif (mod(i,4)==2)
        shiftvalue=3;
    elseif (mod(i,4)==3)
        shiftvalue=2;
    else
        shiftvalue=1;
    end
    input_mat(i,:)=circshift(input_mat(i,:),[0 shiftvalue]);
end
disp('Output After shifting');
%displaying matrix
for i=1:length(input_mat)
    disp(input_mat(i,:));
    if(mod(i,4)==0)
        fprintf('\n');
    end
end

%parity checking and adding
mat_dec=zeros(length(input_mat),4);
%converting to decimal then to binary
for i=1:length(input_mat)
    for k=1:4
        mat_dec(i,k)=((input_mat(i,k)+1)-1);
    end
end
newmat=zeros(1,length(mat_dec)*4);
ter=1;
for i=1:length(mat_dec)
    for k=1:4
        new_mat(ter)=mat_dec(i,k);
        ter=ter+1;
    end
end
new_mat=new_mat';
mat_bin = de2bi(new_mat);
mat_bin=fliplr(mat_bin); % matlab produces reverse binary//hence the binary arrays have to be flipped
%parity adding
mat_bin=[zeros(length(mat_bin),1) mat_bin];

for i=1:length(mat_bin)
    if(mod(sum(mat_bin(i,:)),2)~=0)
        if(mat_bin(i,2)==1)
            mat_bin(i,1)=1;
        else
            mat_bin(i,2)=0;
        end
    end
end
dec_cmat=bi2de(fliplr(mat_bin));
new_cmat=dec2hex(dec_cmat);
disp('Output after parity checking and adding');

% converting to matrix format
parity_mat=[new_cmat(1,:) ' ' new_cmat(2,:) ' ' new_cmat(3,:) ' ' new_cmat(4,:) ' '];
eachRow=[];
for i=5:length(new_cmat)
    eachRow=[eachRow new_cmat(i,:) ' '];
    if (mod(i,4)==0)
        parity_mat=[parity_mat; eachRow];
        eachRow=[];
    end
end
%displaying matrix
for i=1:length(parity_mat)
    disp(parity_mat(i,:));
    if(mod(i,4)==0)
        fprintf('\n');
    end
end

%rgf complex multiplication
dec_mat=dec_cmat(1:4)';
eachRow=[];
for ii=5:length(dec_cmat)
    eachRow=[eachRow dec_cmat(ii)];
    if(mod(ii,4)==0)
        dec_mat=[dec_mat; eachRow];
        eachRow=[];
    end
end
for cols=1:4
    for subcols=0:(length(dec_mat)/4)-1
        a=dec_mat(1+subcols*4,cols);
        b=dec_mat(2+subcols*4,cols);
        c=dec_mat(3+subcols*4,cols);
        d=dec_mat(4+subcols*4,cols);
        
        ai=bitxor(bitxor(bitxor(rgf(a,2),rgf(b,3)),c),d);
        bi=bitxor(bitxor(bitxor(a,rgf(b,2)),(rgf(c,3))),d);
        ci=bitxor( bitxor(a,b) ,  bitxor(rgf(c,2),rgf(d,3) ));
        di=bitxor(bitxor(rgf(a,3),b) , bitxor(c,rgf(d,2)) );
        
        
        output(1+subcols*4,cols)=ai;
        output(2+subcols*4,cols)=bi;
        output(3+subcols*4,cols)=ci;
        output(4+subcols*4,cols)=di;
    end
end

disp('Output after rgf complex multiplication');
for i=1:length(output)
    anw=dec2hex(output(i,:));
    outputi(i,1:8)=[anw(1,:) anw(2,:) anw(3,:) anw(4,:)];
end

for i=1:length(outputi)
    fprintf('%c%c %c%c %c%c %c%c',outputi(i,1),outputi(i,2),outputi(i,3),outputi(i,4),outputi(i,5),outputi(i,6),outputi(i,7),outputi(i,8));
    fprintf('\n');
    if(mod(i,4)==0)
        fprintf('\n');
    end
end

diary off;
