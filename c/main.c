#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef unsigned char u1;
typedef unsigned short u2;
typedef unsigned int u4;
typedef unsigned long long u8;


#define READ_U1(v, p, l)  v = *(p)++
#define READ_U2(v, p, l)  v = ((p)[0]<<8)|(p)[1]; (p)+=2
#define READ_U4(v, p, l)  v = ((p)[0]<<24)|((p)[1]<<16)|((p)[2]<<8)|(p)[3]; (p)+=4
#define READ_U8(v, p, l)  v = ((u8)(p)[0]<<56)|((u8)(p)[1]<<48)|((u8)(p)[2]<<40) \
                              |((u8)(p)[3]<<32)|((u8)(p)[4]<<24)|((u8)(p)[5]<<16) \
|((u8)(p)[6]<<8)|(u8)(p)[7]; (p)+=8
#define SKIP_U2(v, p, l)  (p)+=2

#define READ_INDEX(v, p, l)               READ_U2(v,p,l)
#define READ_TYPE_INDEX(v, cp, t, p, l)     READ_U2(v,p,l)

#define READ_RAW(v, p, l)  memcpy((void *)v, (void *)p, l); (p)+=l;

typedef struct constant_item {
  u1 tag;
  u1 *raw;
} ConstantItem;

int main(int argc, char **argv) {

  char *exe = argv[0];
  char *cls = argv[1];
  char *p1 = argv[2];
  char *p2 = argv[3];

  // printf("%s %s %s %s\n", exe, cls, p1, p2);

  FILE *fd = fopen("../misc/Hello.class", "r");
  if ( fd == NULL ) {
    exit(-1);
  }

  fseek(fd, 0L, SEEK_END);
  int file_len = ftell(fd);
  fseek(fd, 0L, SEEK_SET);

  // printf("file len: %d\n", file_len);

  char *data = malloc(file_len);
  int read_len = fread(data, sizeof(char), file_len, fd);
  fclose(fd);
  // printf("read len: %d\n", file_len);

  if ( read_len != file_len ) {
    exit(-2);
  }

  // read byte
  u1 *ptr = (u1 *)data;
  int magic;
  READ_U4(magic, ptr, read_len);
  if (magic != 0xcafebabe) {
    printf("bad magic");
    exit(-3);
  }
  printf("0x%X\n", magic);
  (ptr)+=2; // mv
  (ptr)+=2; // mv

  int cp_count;
  READ_U2(cp_count, ptr, read_len);
  printf("cp cnt: %d\n", cp_count);

  // read constant pool
  ConstantItem *cp = malloc(sizeof(ConstantItem) * cp_count);
  for (int i = 1; i < cp_count; i ++ ) {
    u1 tag;
    READ_U1(tag, ptr, read_len);
    printf("tag: %d\n", tag);

    if (tag == 10) { // method ref
      u1 *raw = malloc(4);
      READ_RAW(raw, ptr, 4);
      u2 x;
      u2 y;
      READ_U2(x, raw, 4);
      READ_U2(y, raw, 4);

      printf("i: %d %d\n",x ,y);
      continue;
    }
    exit(-3);
  }
}
